package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.request.VariantOptionCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.PageResponseDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionResponseDTO;
import com.umutyenidil.atlas.entity.VariantOption;
import com.umutyenidil.atlas.exception.NotFoundException;
import com.umutyenidil.atlas.exception.ValidationException;
import com.umutyenidil.atlas.repository.VariantOptionRepository;
import com.umutyenidil.atlas.service.VariantOptionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultVariantOptionServiceTest {

    @Mock
    private VariantOptionRepository variantOptionRepository;

    @Mock
    private VariantOptionMapper variantOptionMapper;

    @InjectMocks
    private DefaultVariantOptionService variantOptionService;

    @Test
    @DisplayName("Should throw ValidationException if a variant option with the same name already exists")
    public void createVariantOption_WhenAnyVariantOptionExistsWithName_ShouldThrowValidationException() {
        // Arrange
        var request = new VariantOptionCreateRequestDTO("Color");
        VariantOption existingOption = VariantOption.builder().name("Color").build();

        when(variantOptionRepository.findByName(anyString())).thenReturn(Optional.of(existingOption));

        // Act && Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> variantOptionService.createVariantOption(request)
        );

        assertEquals("name", exception.getSubject());
        assertEquals("validation.variant-option.name.alreadyExists", exception.getMessageKey());

        verify(variantOptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save and return VariantOptionResponseDTO when name is unique")
    public void createVariantOption_WhenNameIsUnique_ShouldSaveAndReturnResponse() {
        // Arrange
        var request = new VariantOptionCreateRequestDTO("Size");
        VariantOption savedEntity = VariantOption.builder().id(UUID.randomUUID()).name("Size").build();
        VariantOptionResponseDTO expectedResponse = VariantOptionResponseDTO.builder()
                .id(savedEntity.getId().toString())
                .name("Size")
                .build();

        when(variantOptionRepository.findByName("Size")).thenReturn(Optional.empty());
        when(variantOptionRepository.save(any(VariantOption.class))).thenReturn(savedEntity);
        when(variantOptionMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // Act
        VariantOptionResponseDTO actualResponse = variantOptionService.createVariantOption(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());

        verify(variantOptionRepository, times(1)).save(any(VariantOption.class));
    }

    @Test
    @DisplayName("Should return mapped PageResponseDTO when getting variant options")
    public void getVariantOptions_ShouldReturnMappedPageResponse() {
        // Arrange
        int page = 0;
        int size = 10;
        VariantOption option = VariantOption.builder().id(UUID.randomUUID()).name("Material").build();
        Page<VariantOption> entityPage = new PageImpl<>(List.of(option));

        PageResponseDTO<VariantOptionResponseDTO> expectedResponse = PageResponseDTO.<VariantOptionResponseDTO>builder()
                .items(List.of(VariantOptionResponseDTO.builder().id(option.getId().toString()).name("Material").build()))
                .build();

        when(variantOptionRepository.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(variantOptionMapper.toPageResponse(entityPage)).thenReturn(expectedResponse);

        // Act
        PageResponseDTO<VariantOptionResponseDTO> actualResponse = variantOptionService.getVariantOptions(page, size);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(variantOptionRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return VariantOptionResponseDTO when valid ID exists")
    public void getVariantOption_WhenIdExists_ShouldReturnResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        VariantOption option = VariantOption.builder().id(id).name("Color").build();
        VariantOptionResponseDTO expectedResponse = VariantOptionResponseDTO.builder()
                .id(id.toString())
                .name("Color")
                .build();

        when(variantOptionRepository.findById(id)).thenReturn(Optional.of(option));
        when(variantOptionMapper.toResponse(option)).thenReturn(expectedResponse);

        // Act
        VariantOptionResponseDTO actualResponse = variantOptionService.getVariantOption(id.toString());

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        verify(variantOptionRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw NotFoundException when ID does not exist")
    public void getVariantOption_WhenIdDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(variantOptionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> variantOptionService.getVariantOption(id.toString())
        );

        assertEquals("VARIANT_OPTION", exception.getSubject());
        assertEquals("exception.variant-option.notFound", exception.getMessageKey());
        verify(variantOptionMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when ID is not a valid UUID format")
    public void getVariantOption_WhenIdIsInvalidUUID_ShouldThrowIllegalArgumentException() {
        // Arrange
        String invalidId = "invalid-uuid-string";

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> variantOptionService.getVariantOption(invalidId)
        );

        verify(variantOptionRepository, never()).findById(any());
    }
}