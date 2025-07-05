package ru.amplicode.orders.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.amplicode.orders.domain.Product;
import ru.amplicode.orders.rest.dto.CreateProductDto;
import ru.amplicode.orders.rest.dto.ProductDto;
import ru.amplicode.orders.rest.mapper.ProductMapper;
import ru.amplicode.orders.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    private final ProductMapper mapper;

    @GetMapping
    public PagedModel<ProductDto> getAll(Pageable pageable) {
        Page<Product> products = service.getAll(pageable);
        return new PagedModel<>(
            products.map(mapper::toProductDto)
        );
    }

    @GetMapping("/{id}")
    public ProductDto getOne(@PathVariable Long id) {
        return mapper.toProductDto(service.getOne(id));
    }

    @GetMapping("/by-ids")
    public List<ProductDto> getMany(@RequestParam List<Long> ids) {
        return service.getMany(ids)
            .stream()
            .map(mapper::toProductDto)
            .toList();
    }

    @PostMapping
    public ProductDto create(@RequestBody CreateProductDto product) {
        return mapper.toProductDto(
                service.create(product)
        );
    }

    @PostMapping("/{id}/rename")
    public ProductDto renameProduct(@PathVariable Long id, @RequestParam String name) {
        return mapper.toProductDto(
                service.rename(id, name)
        );
    }

    @DeleteMapping("/{id}")
    public ProductDto delete(@PathVariable Long id) {
        Product product = service.delete(id);
        if (product != null) {
            return mapper.toProductDto(product);
        } else {
            return null;
        }
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        service.deleteMany(ids);
    }
}
