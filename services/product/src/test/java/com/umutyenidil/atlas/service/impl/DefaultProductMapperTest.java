package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.response.ProductResponseDTO;
import com.umutyenidil.atlas.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Mapper Unit Tests")
public class DefaultProductMapperTest {

    private DefaultProductMapper defaultProductMapper;

    @BeforeEach
    void setUp() {
        defaultProductMapper = new DefaultProductMapper();
    }

    @Test
    @DisplayName("toResponse() should map Product to ProductResponseDTO when all fields are populated")
    public void toResponse_WhenAllFieldsPopulated_ShouldReturnResponseDTO() {
        // Arrange
        UUID productId = UUID.randomUUID();
        String productName = "iPhone 16 Pro Max";
        String productDescription = "A smart phone.";

        Product product = Product.builder()
                .id(productId)
                .name(productName)
                .description(productDescription)
                .build();

        // Act
        ProductResponseDTO response = defaultProductMapper.toResponse(product);

        // Assert
        assertNotNull(response);
        assertEquals(productId.toString(), response.id());
        assertEquals(productName, response.name());

        assertTrue(response.description().isPresent());
        assertEquals(productDescription, response.description().get());
    }

    @Test
    @DisplayName("toResponse() should handle null description gracefully and return an empty Optional")
    public void toResponse_WhenDescriptionIsNull_ShouldReturnEmptyOptionalDescription() {
        // Arrange
        UUID productId = UUID.randomUUID();
        String productName = "Basic T-Shirt";

        Product product = Product.builder()
                .id(productId)
                .name(productName)
                .description(null)
                .build();

        // Act
        ProductResponseDTO response = defaultProductMapper.toResponse(product);

        // Assert
        assertNotNull(response);
        assertEquals(productId.toString(), response.id());
        assertEquals(productName, response.name());

        assertTrue(response.description().isEmpty());
    }
}