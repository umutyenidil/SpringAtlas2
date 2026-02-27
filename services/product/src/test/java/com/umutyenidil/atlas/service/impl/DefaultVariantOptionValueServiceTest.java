package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.request.VariantOptionValueCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionValueResponseDTO;
import com.umutyenidil.atlas.entity.VariantOption;
import com.umutyenidil.atlas.entity.VariantOptionValue;
import com.umutyenidil.atlas.exception.ConflictException;
import com.umutyenidil.atlas.exception.ValidationException;
import com.umutyenidil.atlas.repository.VariantOptionRepository;
import com.umutyenidil.atlas.repository.VariantOptionValueRepository;
import com.umutyenidil.atlas.service.VariantOptionValueMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultVariantOptionValueServiceTest {

    @Mock
    private VariantOptionRepository variantOptionRepository;

    @Mock
    private VariantOptionValueRepository variantOptionValueRepository;

    @Mock
    private VariantOptionValueMapper variantOptionValueMapper;

    @InjectMocks
    private DefaultVariantOptionValueService variantOptionValueService;

    @Test
    @DisplayName("Should save and return VariantOptionValueResponseDTO when request is valid")
    public void createVariantOptionValue_WhenRequestIsValid_ShouldSaveAndReturnResponse() {
        // Arrange
        UUID variantOptionId = UUID.randomUUID();
        String valueName = "Red";
        var request = new VariantOptionValueCreateRequestDTO(variantOptionId.toString(), valueName);

        VariantOption existingOption = VariantOption.builder().id(variantOptionId).name("Color").build();
        VariantOptionValue savedValue = VariantOptionValue.builder()
                .id(UUID.randomUUID())
                .variantOption(existingOption)
                .name(valueName)
                .build();
        VariantOptionValueResponseDTO expectedResponse = VariantOptionValueResponseDTO.builder()
                .id(savedValue.getId().toString())
                .name(valueName)
                .build();

        when(variantOptionRepository.findById(variantOptionId)).thenReturn(Optional.of(existingOption));
        when(variantOptionValueRepository.findByVariantOption_IdAndName(variantOptionId, valueName)).thenReturn(Optional.empty());
        when(variantOptionValueRepository.save(any(VariantOptionValue.class))).thenReturn(savedValue);
        when(variantOptionValueMapper.toResponse(savedValue)).thenReturn(expectedResponse);

        // Act
        VariantOptionValueResponseDTO actualResponse = variantOptionValueService.createVariantOptionValue(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());

        verify(variantOptionValueRepository, times(1)).save(any(VariantOptionValue.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when the parent VariantOption does not exist")
    public void createVariantOptionValue_WhenVariantOptionNotFound_ShouldThrowValidationException() {
        // Arrange
        UUID variantOptionId = UUID.randomUUID();
        var request = new VariantOptionValueCreateRequestDTO(variantOptionId.toString(), "Red");

        when(variantOptionRepository.findById(variantOptionId)).thenReturn(Optional.empty());

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> variantOptionValueService.createVariantOptionValue(request)
        );

        assertEquals("variantOptionId", exception.getSubject());
        assertEquals("exception.variant-option.notFound", exception.getMessageKey());

        verify(variantOptionValueRepository, never()).findByVariantOption_IdAndName(any(), anyString());
        verify(variantOptionValueRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when a value with the same name already exists for the option")
    public void createVariantOptionValue_WhenValueNameAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        UUID variantOptionId = UUID.randomUUID();
        String valueName = "Red";
        var request = new VariantOptionValueCreateRequestDTO(variantOptionId.toString(), valueName);

        VariantOption existingOption = VariantOption.builder().id(variantOptionId).build();
        VariantOptionValue existingValue = VariantOptionValue.builder().name(valueName).build();

        when(variantOptionRepository.findById(variantOptionId)).thenReturn(Optional.of(existingOption));
        when(variantOptionValueRepository.findByVariantOption_IdAndName(variantOptionId, valueName)).thenReturn(Optional.of(existingValue));

        // Act & Assert
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> variantOptionValueService.createVariantOptionValue(request)
        );

        assertEquals("VARIANT_OPTION_VALUE", exception.getSubject());
        assertEquals("exception.variant-option-value.alreadyExists", exception.getMessageKey());

        verify(variantOptionValueRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when variantOptionId is not a valid UUID format")
    public void createVariantOptionValue_WhenVariantOptionIdIsInvalidUUID_ShouldThrowIllegalArgumentException() {
        // Arrange
        var request = new VariantOptionValueCreateRequestDTO("invalid-uuid-format", "Red");

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> variantOptionValueService.createVariantOptionValue(request)
        );

        verify(variantOptionRepository, never()).findById(any());
        verify(variantOptionValueRepository, never()).save(any());
    }
}