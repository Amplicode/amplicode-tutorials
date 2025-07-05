package ru.amplicode.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.amplicode.orders.domain.*;
import ru.amplicode.orders.repository.CustomerRepository;
import ru.amplicode.orders.repository.OrderLineRepository;
import ru.amplicode.orders.repository.OrderRepository;
import ru.amplicode.orders.repository.ProductRepository;
import ru.amplicode.orders.rest.dto.*;
import ru.amplicode.orders.rest.filter.OrderFilter;
import ru.amplicode.orders.rest.mapper.OrderLineMapper;
import ru.amplicode.orders.rest.mapper.OrderMapper;
import ru.amplicode.orders.service.dto.OrderDeliveryInfoDto;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final OrderLineRepository orderLineRepository;

    private final OrderLineMapper orderLineMapper;

//    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    private final InventoryService inventoryService;

    public OrderDto create(CreateOrderDto orderDto) {
        Optional<Order> existing = orderRepository.findFirstByCustomerAndOrderStatus_OrderByCreatedDateDesc(
            customerRepository.getReferenceById(orderDto.customerId()),
            OrderStatus.NEW
        );

        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return orderMapper.toOrderDto(
            orderRepository.save(orderMapper.toEntity(orderDto)));
    }

    public Optional<OrderWithLinesDto> getActiveOrder(Long customerId) {
        Customer customer = customerRepository.getReferenceById(customerId);

        return orderRepository.findFirstByCustomerAndOrderStatus_OrderByCreatedDateDesc(customer, OrderStatus.NEW)
            .map(orderMapper::toOrderWithLinesDto);
    }

    public OrderLineDto addProduct(Long orderId, Long productId, Long amount) {
        Order order = getOrderOrThrow(orderId);

        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order lines is readonly");
        }

        Product product = productRepository.getReferenceById(productId);

        // todo update inventory
        inventoryService.reserve(product, order.getCity(), amount);

        OrderLine orderLine = order.getOrderLines().stream()
            .filter(line -> line.getProduct().getId().equals(productId))
            .findFirst()
            .orElseGet(() -> {
                OrderLine line = new OrderLine();
                line.setOrder(order);
                line.setProduct(product);
                line.setAmount(0L);
                order.getOrderLines().add(line);
                return line;
            });

        orderLine.setAmount(orderLine.getAmount() + amount);
        orderLineRepository.saveAndFlush(orderLine);

        recalculateOrderSum(order);

        orderRepository.saveAndFlush(order);

        return orderLineMapper.toOrderLineDto(orderLine);
    }

    public OrderLineDto changeProductsAmount(Long orderLineId, OrderLineAmount amount) {
        OrderLine orderLine = orderLineRepository.findById(orderLineId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Order order = orderLine.getOrder();
        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        orderLine.setAmount(amount.getAmount());

        orderLineRepository.saveAndFlush(orderLine);

        order = orderLine.getOrder();
        recalculateOrderSum(order);

        orderRepository.save(order);

        return orderLineMapper.toOrderLineDto(orderLine);
    }

    @Transactional
    public void deleteProduct(Long orderLineId) {
        orderLineRepository.findById(orderLineId)
            .ifPresent(line -> {
                inventoryService.cancelReserve(line.getProduct(), null, line.getAmount());

                recalculateOrderSum(line.getOrder());
            });

        orderLineRepository.deleteById(orderLineId);
    }

    @Transactional
    public OrderDto payOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        order.setOrderStatus(OrderStatus.PAID);

        OrderDto orderDto = orderMapper.toOrderDto(order);

//        kafkaTemplate.send("order-pay", orderDto);

        return orderDto;
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        order.getOrderLines().forEach(line -> {
            inventoryService.cancelReserve(line.getProduct(), order.getCity(), line.getAmount());
        });

        order.setOrderStatus(OrderStatus.CANCELED);
    }

//    @KafkaListener(topics = "orderDelivery", containerFactory = "orderDeliveryInfoDtoListenerFactory")
//    @Transactional
    public void consumeOrderDeliveryInfoDto(OrderDeliveryInfoDto orderDeliveryInfoDto) {
        Order order = getOrderOrThrow(orderDeliveryInfoDto.id());
        if (orderDeliveryInfoDto.delivered()) {
            order.setOrderStatus(OrderStatus.DELIVERED);

            order
                .getOrderLines()
                .forEach(line -> {
                    inventoryService.productShipped(line.getProduct(), order.getCity(), line.getAmount());
                });
        } else {
            order.setOrderStatus(OrderStatus.CANCELED);

            order
                .getOrderLines()
                .forEach(line -> {
                    inventoryService.cancelReserve(line.getProduct(), order.getCity(), line.getAmount());
                });
        }
    }

    private Order getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id %s not found".formatted(orderId))
            );
    }

    public Page<OrderWithLinesDto> getAll(OrderFilter filter, Pageable pageable) {
        Specification<Order> spec = filter.toSpecification();
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(orderMapper::toOrderWithLinesDto);
    }

    private void recalculateOrderSum(Order order) {
        order.setSum(
            order.getOrderLines().stream()
                .map(orderLine -> orderLine.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(orderLine.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
