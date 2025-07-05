package ru.amplicode.orders.service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ru.amplicode.orders.domain.City;
import ru.amplicode.orders.domain.Inventory;
import ru.amplicode.orders.domain.Product;
import ru.amplicode.orders.repository.CityRepository;
import ru.amplicode.orders.repository.InventoryRepository;
import ru.amplicode.orders.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;

    private final InventoryRepository inventoryRepository;

    private final CityRepository cityRepository;

    @Transactional
    public Inventory supply(Long productId, Long cityId, Long amount) {
        Product product = productRepository.getReferenceById(productId);
        City city = cityRepository.getReferenceById(cityId);

        Inventory inventory = inventoryRepository.findByProductAndCity(product, city)
            .orElseGet(() -> {
                Inventory created = new Inventory();
                created.setProduct(product);
                created.setCity(city);
                created.setReserved(0L);
                created.setAvailable(0L); // demo: possible bug if not initialized
                return inventoryRepository.save(created);
            });

        inventory.setAvailable(inventory.getAvailable() + amount);

        return inventory;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void reserve(Product product, City city, Long amount) {
        Inventory inventory = inventoryRepository.findByProductAndCity(product, city)
            .filter(t -> amount < t.getAvailable()) // demo: change sign
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Unable to reserve product"));

        inventory.setAvailable(inventory.getAvailable() - amount);
        inventory.setReserved(inventory.getReserved() + amount);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void productShipped(Product product, City city, Long amount) {
        Inventory inventory = inventoryRepository.findByProductAndCity(product, city)
            .orElseThrow();

        inventory.setReserved(inventory.getReserved() - amount);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelReserve(Product product, City city, Long amount) {
        Optional<Inventory> maybeInventory = inventoryRepository.findByProductAndCity(product, city);
        if (maybeInventory.isEmpty()) {
            return;
        }
        Inventory inventory = maybeInventory
            .orElseThrow();

        if (amount > inventory.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        inventory.setAvailable(inventory.getAvailable() + amount);
        inventory.setReserved(inventory.getReserved() - amount);
    }

    public Page<Inventory> loadProductInventories(Long productId, Pageable pageable) {
        return inventoryRepository.findByProduct(productRepository.getReferenceById(productId), pageable);
    }
}
