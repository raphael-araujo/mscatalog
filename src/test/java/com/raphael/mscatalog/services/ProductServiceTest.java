package com.raphael.mscatalog.services;

import com.raphael.mscatalog.dto.ProductCreateDTO;
import com.raphael.mscatalog.dto.ProductResponseDTO;
import com.raphael.mscatalog.entities.Product;
import com.raphael.mscatalog.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException.UnprocessableEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private ProductCreateDTO createDTO;
    private ProductCreateDTO updateDTO;

    @BeforeEach
    public void setUp() {

        createDTO = new ProductCreateDTO(
                "Monitor",
                "Monitor IPS de 27 Polegadas",
                960.0
        );
        product1 = new Product(createDTO);

    }

    @Test
    @DisplayName("Given product object when Create product then return product object")
    void testGivenProductObject_WhenCreateProduct_thenReturnProductObject() {

        given(productRepository.save(product1)).willReturn(product1);

        ProductResponseDTO savedProduct = productService.create(createDTO);

        assertNotNull(savedProduct);
        assertEquals(product1.getName(), savedProduct.getName());
        assertEquals(product1.getPrice(), savedProduct.getPrice());
        assertEquals(product1.getDescription(), savedProduct.getDescription());
    }

    @Test
    @DisplayName("Given product object when Create product with empty fields then return exception")
    void testGivenProductObject_WhenCreateProductWithEmptyFields_thenReturnException() {

        var invalidProduct = new Product(null, "", "", 0D);

        given(productRepository.save(invalidProduct)).willThrow(UnprocessableEntity.class);

        assertThatThrownBy(() -> productService.create(new ProductCreateDTO())).isInstanceOf(UnprocessableEntity.class);
    }

    @Test
    @DisplayName("Given ProductId when findById then return product object")
    void testGivenProductId_WhenFindById_thenReturnProductObject() {

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product1));

        ProductResponseDTO product = productService.findById(1L);

        assertNotNull(product);
        assertEquals("Monitor", product.getName());
        assertEquals("Monitor IPS de 27 Polegadas", product.getDescription());
        assertEquals(960.0, product.getPrice());
    }

    @Test
    @DisplayName("Given non existent ProductId when findById then return empty")
    void testGivenNonExistentProductId_WhenFindById_thenReturnEmpty() {

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class).hasMessage("Produto com id 1 não encontrado");
    }

    @Test
    @DisplayName("Given products list when findAll products then return products")
    void testGivenProductsList_WhenFindAllProducts_thenReturnProductsList() {

        Product product2 = new Product(
                null, "Placa mãe B650M",
                "Plataforma AM5", 750.0
        );
        given(productRepository.findAll()).willReturn(List.of(product1, product2));

        List<ProductResponseDTO> productList = productService.findAll();

        assertNotNull(productList);
        assertEquals(2, productList.size());
    }

    @Test
    @DisplayName("Given empty products list when findAll products then return empty products list")
    void testGivenEmptyProductsList_WhenFindAllProducts_thenReturnEmptyProductsList() {

        given(productRepository.findAll()).willReturn(Collections.emptyList());

        List<ProductResponseDTO> productList = productService.findAll();

        assertTrue(productList.isEmpty());
    }

    @Test
    @DisplayName("Given Product Object when Update product then return updated product object")
    void testGivenProductObject_WhenUpdateProduct_thenReturnUpdatedProductObject() {

        given(productRepository.findById(1L)).willReturn(Optional.of(product1));

        updateDTO = new ProductCreateDTO(
                "Gabinete",
                "Gabinete Gamer com 6 fans",
                250.0
        );
        ProductResponseDTO updatedProduct = productService.update(1L, updateDTO);

        assertNotNull(updatedProduct);
        assertEquals("Gabinete", updatedProduct.getName());
        assertEquals("Gabinete Gamer com 6 fans", updatedProduct.getDescription());
        assertEquals(250, updatedProduct.getPrice());
    }

    @Test
    @DisplayName("Given non existent ProductId when Update then return empty")
    void testGivenNonExistentProductId_WhenUpdate_thenReturnEmpty() {

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        updateDTO = new ProductCreateDTO(
                "Gabinete",
                "Gabinete Gamer com 6 fans",
                250.0
        );
        assertThatThrownBy(() -> productService.update(1L, updateDTO))
                .isInstanceOf(EntityNotFoundException.class).hasMessage("Produto com id 1 não encontrado");
    }

    @Test
    @DisplayName("Given ProductId when Delete product then do nothing")
    void testGivenProductId_WhenDeleteProduct_thenDoNothing() {

        product1.setId(1L);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product1));
        willDoNothing().given(productRepository).delete(product1);

        productService.delete(product1.getId());

        verify(productRepository, times(1)).delete(product1);
    }

    @Test
    @DisplayName("Given non existent ProductId when Delete then return empty")
    void testGivenNonExistentProductId_WhenDelete_thenReturnEmpty() {

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class).hasMessage("Produto com id 1 não encontrado");
    }

    @Test
    @DisplayName("Given name or description and price when findByNameOrDescriptionAndPrice then return product list")
    void testGivenNameOrDescriptionAndPrice_whenFindByNameOrDescriptionAndPrice_thenReturnProductList() {

        given(productRepository.findByNameOrDescriptionAndPrice("ips", 0D, 999D))
                .willReturn(List.of(product1));

        List<ProductResponseDTO> productList = productService.search("ips", 0D, 999D);

        assertNotNull(productList);
        assertEquals(1, productList.size());
        assertEquals("Monitor", productList.get(0).getName());
    }

    @Test
    @DisplayName("Given name or description and price when findByNameOrDescriptionAndPrice then return empty product list")
    void testGivenNameOrDescriptionAndPrice_whenFindByNameOrDescriptionAndPrice_thenReturnEmptyProductList() {

        given(productRepository.findByNameOrDescriptionAndPrice(anyString(), anyDouble(), anyDouble()))
                .willReturn(Collections.emptyList());

        List<ProductResponseDTO> productList = productService.search("ips", 0D, 999D);

        assertNotNull(productList);
        assertEquals(0, productList.size());
    }
}