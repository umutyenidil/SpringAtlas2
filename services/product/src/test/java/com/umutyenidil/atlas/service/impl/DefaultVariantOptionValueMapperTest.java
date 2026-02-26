package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.response.VariantOptionValueResponseDTO;
import com.umutyenidil.atlas.entity.VariantOption;
import com.umutyenidil.atlas.entity.VariantOptionValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefaultVariantOptionValueMapperTest {

    private DefaultVariantOptionValueMapper defaultVariantOptionValueMapper;

    @BeforeEach
    void setUp() {
        defaultVariantOptionValueMapper = new DefaultVariantOptionValueMapper();
    }

    @Test
    @DisplayName("Should successfully map VariantOpitonValue entity to VariantOptionValueResponseDTO")
    public void toResponse_ShouldReturnVariantOptionValueResponseDTO() {

        // Arrange
        UUID id = UUID.randomUUID();
        UUID variantOptionId = UUID.randomUUID();
        String name = "Red";

        VariantOptionValue variantOptionValue = VariantOptionValue.builder()
                .id(id)
                .variantOption(
                        VariantOption.builder()
                                .id(variantOptionId)
                                .build()
                )
                .name(name)
                .build();

        // Act
        VariantOptionValueResponseDTO response = defaultVariantOptionValueMapper.toResponse(variantOptionValue);

        // Assert
        assertNotNull(response);

        assertEquals(id.toString(), response.id());
        assertEquals(variantOptionId.toString(), response.variantOptionId());
        assertEquals(name, response.name());
    }
}
