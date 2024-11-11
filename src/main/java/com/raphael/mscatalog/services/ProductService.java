package com.raphael.mscatalog.services;

import com.raphael.mscatalog.dto.ProductCreateDTO;
import com.raphael.mscatalog.dto.ProductResponseDTO;
import com.raphael.mscatalog.entities.Product;
import com.raphael.mscatalog.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        var product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Produto com id %s não encontrado", id))
        );
        return new ProductResponseDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        List<ProductResponseDTO> productResponseDTOs = new ArrayList<>();

        for (Product product : productRepository.findAll()) {
            productResponseDTOs.add(new ProductResponseDTO(product));
        }
        return productResponseDTOs;
    }
}
