package ru.amplicode.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.amplicode.orders.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(nativeQuery = true, value = "select * from product p where p.price > :price")
    List<Product> findByPrice(BigDecimal price);
}
