package com.raphael.mscatalog.repositories;

import com.raphael.mscatalog.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;

    @BeforeEach
    public void setUp() {

        product1 = new Product(
                null, "Monitor",
                "Monitor IPS de 27 Polegadas", 960.0
        );
    }

    @Test
    @DisplayName("Given product object when save then return saved product")
    void testGivenProductObject_whenSave_thenReturnSavedProduct() {
        Product savedProduct = productRepository.save(product1);

        assertNotNull(savedProduct);
        assertTrue(savedProduct.getId() > 0);
    }

    @Test
    @DisplayName("Given product object when findByID then return product object")
    void testGivenProductObject_whenFindByID_thenReturnProductObject() {

        productRepository.save(product1);

        Product savedProduct = productRepository.findById(product1.getId()).get();

        assertNotNull(savedProduct);
        assertEquals(product1.getId(), savedProduct.getId());
    }

    @Test
    @DisplayName("Given product list when findAll then return product list")
    void testGivenProductObject_whenFindAll_thenReturnProductList() {

        Product product2 = new Product(
                null, "Placa mãe B650M",
                "Plataforma AM5", 750.0
        );
        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> productList = productRepository.findAll();

        assertNotNull(productList);
        assertEquals(2, productList.size());
    }

    @Test
    @DisplayName("Given product object when update product then return updated product object")
    void testGivenProductObject_whenUpdateProduct_thenReturnUpdatedProductObject() {

        productRepository.save(product1);

        Product savedProduct = productRepository.findById(product1.getId()).get();
        savedProduct.setName("Gabinete");
        savedProduct.setDescription("Gabinete Gamer com 6 fans");
        savedProduct.setPrice(250.0);
        Product updatedProduct = productRepository.save(savedProduct);

        assertNotNull(updatedProduct);
        assertEquals("Gabinete", updatedProduct.getName());
        assertEquals("Gabinete Gamer com 6 fans", updatedProduct.getDescription());
        assertEquals(250.0, updatedProduct.getPrice());
    }

    @Test
    @DisplayName("Given product object when delete then remove product")
    void testGivenProductObject_whenDeleteProduct_thenRemoveProduct() {

        productRepository.save(product1);

        productRepository.delete(product1);

        Optional<Product> productOptional = productRepository.findById(product1.getId());

        assertTrue(productOptional.isEmpty());
    }

    @Test
    @DisplayName("Given name or description and price when findByNameOrDescriptionAndPrice then return product list")
    void testGivenNameOrDescriptionAndPrice_whenFindByNameOrDescriptionAndPrice_thenReturnProductList() {

        Product product2 = new Product(
                null, "Placa mãe Maxsun B650M",
                "Plataforma AM5", 750.0
        );
        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> savedProducts = productRepository.findByNameOrDescriptionAndPrice("ips", 0D, 999D);

        assertNotNull(savedProducts);
        assertEquals(1, savedProducts.size());
        assertEquals(product1.getName(), savedProducts.get(0).getName());
        assertEquals(product1.getDescription(), savedProducts.get(0).getDescription());
        assertEquals(product1.getPrice(), savedProducts.get(0).getPrice());
    }
}
