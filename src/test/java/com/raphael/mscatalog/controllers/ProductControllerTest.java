package com.raphael.mscatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.mscatalog.dto.ProductCreateDTO;
import com.raphael.mscatalog.dto.ProductResponseDTO;
import com.raphael.mscatalog.entities.Product;
import com.raphael.mscatalog.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private Product product1;
    private ProductCreateDTO createDTO;
    private ProductCreateDTO updateDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    public void setUp() {

        createDTO = new ProductCreateDTO(
                "Monitor",
                "Monitor IPS de 27 Polegadas",
                960.0
        );
        product1 = new Product(createDTO);
        productResponseDTO = new ProductResponseDTO(product1);
    }

    @Test
    @DisplayName("Given product object when create product then return saved product")
    void testGivenProductObject_WhenCreateProduct_thenReturnSavedProduct() throws Exception {

        given(productService.create(any(ProductCreateDTO.class)))
                .willReturn(productResponseDTO)
                .willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(product1.getName())))
                .andExpect(jsonPath("$.description", is(product1.getDescription())))
                .andExpect(jsonPath("$.price", is(product1.getPrice())));
    }

    @Test
    @DisplayName("Given product object when Create product with invalid fields then return exception")
    void testGivenProductObject_WhenCreateProductWithInvalidFields_thenReturnException() throws Exception {

        var invalidCreateDTO = new ProductCreateDTO("", "Gabinete Gamer com 6 fans", 0D);

        given(productService.create(any(ProductCreateDTO.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCreateDTO)));

        response.andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", is("Campo(s) inválido(s)")))
                .andExpect(jsonPath("$.errors.name", is("O campo 'name' é obrigatório")))
                .andExpect(jsonPath("$.errors.price", is("O preço deve ser maior que zero")));
    }

    @Test
    @DisplayName("Given productId when findById then return product object")
    void testGivenProductId_WhenFindById_thenReturnProductObject() throws Exception {

        long productId = 1L;
        given(productService.findById(productId)).willReturn(productResponseDTO);

        ResultActions response = mockMvc.perform(get("/products/{id}", productId));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(productResponseDTO.getName())))
                .andExpect(jsonPath("$.description", is(productResponseDTO.getDescription())))
                .andExpect(jsonPath("$.price", is(productResponseDTO.getPrice())));
    }

    @Test
    @DisplayName("Given invalid productId when findById then return not found")
    void testGivenInvalidProductId_WhenFindById_thenReturnNotFound() throws Exception {

        long productId = 1L;
        given(productService.findById(productId)).willThrow(EntityNotFoundException.class);

        ResultActions response = mockMvc.perform(get("/products/{id}", productId));

        response.andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("Given invalid parameter when findById then return exception")
    void testGivenInvalidParameter_WhenFindById_thenReturnException() throws Exception {

        given(productService.findById(0L)).willThrow(MethodArgumentTypeMismatchException.class);

        ResultActions response = mockMvc.perform(get("/products/any"));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Parâmetros informados estão inválidos")));
    }

    @Test
    @DisplayName("Given list of products when findAll products then return products list")
    void testGivenListOfProducts_WhenFindAllProducts_thenReturnProductsList() throws Exception {

        var product2 = new Product(
                null, "Placa mãe B650M",
                "Plataforma AM5", 750.0);

        List<ProductResponseDTO> products = new ArrayList<>();

        products.add(productResponseDTO);
        products.add(new ProductResponseDTO(product2));

        given(productService.findAll()).willReturn(products);

        ResultActions response = mockMvc.perform(get("/products"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(products.size())));
    }

    @Test
    @DisplayName("Given empty products list when findAll products then return empty products list")
    void testGivenEmptyProductsList_WhenFindAllProducts_thenReturnEmptyProductsList() throws Exception {

        given(productService.findAll()).willReturn(Collections.emptyList());

        ResultActions response = mockMvc.perform(get("/products"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    @DisplayName("Given updated product when Update then return updated product")
    void testGivenUpdatedProduct_WhenUpdate_thenReturnUpdatedProductObject() throws Exception {

        updateDTO = new ProductCreateDTO(
                "Gabinete",
                "Gabinete Gamer com 6 fans",
                250.0
        );
        var updatedProduct = new Product(updateDTO);
        var responseDto = new ProductResponseDTO(updatedProduct);

        given(productService.update(anyLong(), any(ProductCreateDTO.class)))
                .willReturn(responseDto);

        long productId = 1L;

        ResultActions response = mockMvc.perform(put("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedProduct.getName())))
                .andExpect(jsonPath("$.description", is(updatedProduct.getDescription())))
                .andExpect(jsonPath("$.price", is(updatedProduct.getPrice())));
    }

    @Test
    @DisplayName("Given nonexistent product when Update then return not found")
    void testGivenNonExistentProduct_WhenUpdate_thenReturnNotFound() throws Exception {

        updateDTO = new ProductCreateDTO(
                "Gabinete",
                "Gabinete Gamer com 6 fans",
                250.0
        );
        long productId = 1L;

        given(productService.update(anyLong(), any(ProductCreateDTO.class)))
                .willThrow(new EntityNotFoundException(String.format("Produto com id %s não encontrado", productId)));

        ResultActions response = mockMvc.perform(put("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Produto com id 1 não encontrado")));
    }

    @Test
    @DisplayName("Given productId when Delete then return NoContent")
    void testGivenProductId_WhenDelete_thenReturnNoContent() throws Exception {

        long productId = 1L;
        willDoNothing().given(productService).delete(productId);

        ResultActions response = mockMvc.perform(delete("/products/{id}", productId));

        response.andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @DisplayName("Given non existent productId when Delete then return Not found")
    void testGivenNonExistentProductId_WhenDelete_thenReturnNotFound() throws Exception {

        long productId = 1L;
        willThrow((new EntityNotFoundException("Produto com id 1 não encontrado")))
                .given(productService).delete(productId);

        ResultActions response = mockMvc.perform(delete("/products/{id}", productId));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Produto com id 1 não encontrado")));
    }

    @Test
    @DisplayName("Given name or description and price when Search then return product list")
    void testGivenNameOrDescriptionAndPrice_whenSearch_thenReturnProductList() throws Exception {

        given(productService.search("ips", 0D, 999D))
                .willReturn(List.of(productResponseDTO));

        ResultActions response = mockMvc.perform(get("/products/search?q=ips&min_price=0&max_price=999"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    @DisplayName("Given name or description and price when Search then return empty product list")
    void testGivenNameOrDescriptionAndPrice_whenSearch_thenReturnEmptyProductList() throws Exception {

        given(productService.search(anyString(), anyDouble(), anyDouble()))
                .willReturn(Collections.emptyList());

        ResultActions response = mockMvc.perform(get("/products/search?q=any&min_price=500&max_price=999"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }
}