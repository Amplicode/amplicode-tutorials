package ru.amplicode.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.amplicode.orders.domain.Customer;
import ru.amplicode.orders.domain.Order;
import ru.amplicode.orders.domain.OrderStatus;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query(value = "select ol from OrderLine ol where ol.amount > 5")
    Optional<Order> findFirstByCustomerAndOrderStatus_OrderByCreatedDateDesc(Customer customer, OrderStatus orderStatus);
}
