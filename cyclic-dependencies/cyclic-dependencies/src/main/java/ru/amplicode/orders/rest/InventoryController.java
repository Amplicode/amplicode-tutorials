package ru.amplicode.orders.rest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.amplicode.orders.rest.dto.InventoryDto;
import ru.amplicode.orders.rest.dto.SupplyDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryControllerService inventoryService;

    @GetMapping("/for-product/{productId}")
    public PagedModel<InventoryDto> loadProductInventories(@PathVariable Long productId, Pageable pageable) {
        return inventoryService.loadProductInventories(productId, pageable);
    }

    @PostMapping("/supply")
    public InventoryDto supply(@RequestBody SupplyDto supplyDto) {
        return inventoryService.supply(supplyDto);
    }
}
