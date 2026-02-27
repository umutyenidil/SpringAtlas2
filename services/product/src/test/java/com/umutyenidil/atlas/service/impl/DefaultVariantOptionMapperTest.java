package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.response.PageResponseDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionResponseDTO;
import com.umutyenidil.atlas.entity.VariantOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultVariantOptionMapperTest {

    private DefaultVariantOptionMapper defaultVariantOptionMapper;

    @BeforeEach
    void setUp() {
        defaultVariantOptionMapper = new DefaultVariantOptionMapper();
    }

    @Test
    @DisplayName("Should successfully map VariantOption entity to VariantOptionResponseDTO")
    public void toResponse_ShouldReturnVariantOptionResponseDTO() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String name = "Color";

        VariantOption variantOption = VariantOption.builder()
                .id(id)
                .name(name)
                .build();

        // Act
        VariantOptionResponseDTO response = defaultVariantOptionMapper.toResponse(variantOption);

        // Assert
        assertNotNull(response);

        assertEquals(id.toString(), response.id());
        assertEquals(name, response.name());
    }

    @Test
    @DisplayName("Should successfully map a populated Page<VariantOption> to PageResponseDTO")
    public void toPageResponse_ShouldReturnPageResponseDTO() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        VariantOption option1 = VariantOption.builder()
                .id(id1)
                .name("Color")
                .build();

        UUID id2 = UUID.randomUUID();
        VariantOption option2 = VariantOption.builder()
                .id(id2)
                .name("Size")
                .build();

        List<VariantOption> options = List.of(option1, option2);

        Page<VariantOption> page = new PageImpl<>(options, PageRequest.of(0, 10), 2);

        // Act
        PageResponseDTO<VariantOptionResponseDTO> response = defaultVariantOptionMapper.toPageResponse(page);

        // Arrange
        assertNotNull(response);
        assertEquals(2, response.getItems().size());

        assertEquals(id1.toString(), response.getItems().get(0).id());
        assertEquals(id2.toString(), response.getItems().get(1).id());

        assertEquals(1, response.getPageNumber());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getPageSize());
        assertEquals(2, response.getTotalElements());
    }

    @Test
    @DisplayName("Should return and empty PageResponseDTO when given an empty Page")
    public void toPageResponse_WhenPageIsEmpty_ShouldReturnEmptyPageResponseDTO() {
        // Arrange
        Page<VariantOption> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        // Act
        PageResponseDTO<VariantOptionResponseDTO> response = defaultVariantOptionMapper.toPageResponse(page);

        // Assert
        assertNotNull(response);
        assertTrue(response.getItems().isEmpty());
        assertEquals(1, response.getPageNumber());
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getTotalElements());
    }
}
