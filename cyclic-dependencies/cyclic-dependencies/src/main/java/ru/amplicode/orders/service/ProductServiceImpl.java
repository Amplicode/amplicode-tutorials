package ru.amplicode.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.amplicode.orders.domain.Product;
import ru.amplicode.orders.repository.ProductRepository;
import ru.amplicode.orders.rest.dto.CreateProductDto;
import ru.amplicode.orders.rest.mapper.ProductMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getOne(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @Override
    public List<Product> getMany(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    @Override
    public Product create(CreateProductDto product) {
        return productRepository.save(productMapper.toEntity(product));
    }

    @Override
    public Product delete(Long id) {
        Product product = productRepository.findById(id)
                .orElse(null);
        if (product != null) {
            productRepository.delete(product);
        }
        return product;
    }

    @Override
    public void deleteMany(List<Long> ids) {
        productRepository.deleteAllById(ids);
    }

    @Transactional
    @Override
    public Product rename(Long id,
                          String name) {
        Product product = productRepository.findById(id)
                .orElseThrow();
        product.setName(name);
        return product;
    }
}
