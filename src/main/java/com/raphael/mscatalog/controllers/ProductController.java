package com.raphael.mscatalog.controllers;

import com.raphael.mscatalog.dto.ProductCreateDTO;
import com.raphael.mscatalog.dto.ProductResponseDTO;
import com.raphael.mscatalog.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductCreateDTO createDTO) {
        var newProduct = productService.create(createDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        var product = productService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(product);
    }
}
