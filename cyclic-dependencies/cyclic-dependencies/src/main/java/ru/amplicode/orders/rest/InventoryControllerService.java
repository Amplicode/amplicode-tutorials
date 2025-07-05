package ru.amplicode.orders.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.amplicode.orders.rest.dto.InventoryDto;
import ru.amplicode.orders.rest.dto.SupplyDto;
import ru.amplicode.orders.rest.mapper.InventoryMapper;
import ru.amplicode.orders.service.InventoryService;

@Component
@RequiredArgsConstructor
public class InventoryControllerService {
    private final InventoryService inventoryService;

    private final InventoryMapper inventoryMapper;

    @Transactional
    public PagedModel<InventoryDto> loadProductInventories(Long productId, Pageable pageable) {
        return new PagedModel<>(
            inventoryService.loadProductInventories(productId, pageable)
                .map(inventoryMapper::toInventoryDto)
        );
    }

    public InventoryDto supply(SupplyDto supplyDto) {
        return inventoryMapper.toInventoryDto(
            inventoryService.supply(
                supplyDto.productId(),
                supplyDto.cityId(),
                supplyDto.amount()
            )
        );
    }
}
