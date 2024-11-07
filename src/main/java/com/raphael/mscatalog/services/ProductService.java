package com.raphael.mscatalog.services;

import com.raphael.mscatalog.dto.ProductCreateDTO;
import com.raphael.mscatalog.dto.ProductResponseDTO;
import com.raphael.mscatalog.entities.Product;
import com.raphael.mscatalog.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDTO create(ProductCreateDTO createDTO) {
        Product product = new Product(createDTO);
        productRepository.save(product);
        return new ProductResponseDTO(product);
    }
}
