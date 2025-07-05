package ru.amplicode.orders.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.amplicode.orders.domain.Product;
import ru.amplicode.orders.rest.dto.CreateProductDto;

import java.util.List;

public interface ProductService {
    Page<Product> getAll(Pageable pageable);

    Product getOne(Long id);

    List<Product> getMany(List<Long> ids);

    Product create(CreateProductDto product);

    Product delete(Long id);

    void deleteMany(List<Long> ids);

    @Transactional
    Product rename(Long id,
                   String name);
}
