package ru.amplicode.orders.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.amplicode.orders.domain.Product;
import ru.amplicode.orders.repository.ProductRepository;
import ru.amplicode.orders.rest.dto.CreateProductDto;
import ru.amplicode.orders.rest.dto.ProductDto;
import ru.amplicode.orders.rest.dto.ProductWithPictureDto;
import ru.amplicode.orders.rest.mapper.ProductMapper;
import ru.amplicode.orders.service.file.FileUploader;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final FileUploader fileUploader;
    
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ObjectMapper objectMapper;

    @Override
    public Product patch(Long id,
                         JsonNode patchNode,
                         MultipartFile file) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        ProductWithPictureDto productWithPictureDto = productMapper.toProductWithPictureDto(product);
        objectMapper.readerForUpdating(productWithPictureDto)
                .readValue(patchNode);
        productMapper.updateWithNull(productWithPictureDto, product);
        
        if (file != null) {
            String picture = fileUploader.uploadPhoto(file, id);
            product.setPicture(picture);
        }

        return productRepository.save(product);
    }

    @Override
    public List<Long> patchMany(List<Long> ids,
                                JsonNode patchNode) throws IOException {
        Collection<Product> products = productRepository.findAllById(ids);

        for (Product product : products) {
            ProductDto productDto = productMapper.toProductDto(product);
            objectMapper.readerForUpdating(productDto)
                    .readValue(patchNode);
            productMapper.updateWithNull(productDto, product);
        }

        List<Product> resultProducts = productRepository.saveAll(products);
        return resultProducts.stream()
                .map(Product::getId)
                .toList();
    }

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
