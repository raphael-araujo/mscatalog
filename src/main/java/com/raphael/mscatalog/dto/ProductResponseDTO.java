package com.raphael.mscatalog.dto;

import com.raphael.mscatalog.entities.Product;
import lombok.Data;

@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
    }
}
