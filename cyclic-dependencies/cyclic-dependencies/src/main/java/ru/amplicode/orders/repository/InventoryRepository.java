package ru.amplicode.orders.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.amplicode.orders.domain.City;
import ru.amplicode.orders.domain.Inventory;
import ru.amplicode.orders.domain.Product;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductAndCity(Product product, City city);

    Page<Inventory> findByProduct(Product product, Pageable pageable);
}
