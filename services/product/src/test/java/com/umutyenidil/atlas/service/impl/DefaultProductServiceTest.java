package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.request.ImageCreateRequestDTO;
import com.umutyenidil.atlas.dto.request.ProductCreateRequestDTO;
import com.umutyenidil.atlas.dto.request.VariantCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.ProductResponseDTO;
import com.umutyenidil.atlas.entity.Product;
import com.umutyenidil.atlas.entity.VariantOption;
import com.umutyenidil.atlas.entity.VariantOptionValue;
import com.umutyenidil.atlas.enumeration.StorageProvider;
import com.umutyenidil.atlas.exception.MultipleValidationException;
import com.umutyenidil.atlas.exception.NotFoundException;
import com.umutyenidil.atlas.repository.ProductRepository;
import com.umutyenidil.atlas.repository.ProductVariantRepository;
import com.umutyenidil.atlas.repository.VariantOptionRepository;
import com.umutyenidil.atlas.repository.VariantOptionValueRepository;
import com.umutyenidil.atlas.service.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductVariantRepository productVariantRepository;
    @Mock private ProductMapper productMapper;
    @Mock private VariantOptionRepository variantOptionRepository;
    @Mock private VariantOptionValueRepository variantOptionValueRepository;

    @InjectMocks private DefaultProductService productService;

    @Test
    @DisplayName("Should successfully create a simple product without variants")
    void createProduct_NoVariants_ShouldSaveAndReturnResponse() {
        // Arrange
        var request = new ProductCreateRequestDTO(
                "Basic Mug", "A simple mug", "BrandX", "Mugs",
                List.of(new ImageCreateRequestDTO("img1.png", StorageProvider.S3)),
                null
        );

        Product savedProduct = Product.builder().id(UUID.randomUUID()).build();
        ProductResponseDTO expectedResponse = ProductResponseDTO.builder().id(savedProduct.getId().toString()).build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toResponse(any(Product.class))).thenReturn(expectedResponse);

        // Act
        ProductResponseDTO response = productService.createProduct(request);

        // Assert
        assertNotNull(response);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productVariantRepository, never()).findExistingBarcodes(anyList());
    }

    @Test
    @DisplayName("Should successfully create a complex product with variants and attributes")
    void createProduct_WithValidVariants_ShouldSaveAndReturnResponse() {
        // Arrange
        String optionId = UUID.randomUUID().toString();
        String valueId1 = UUID.randomUUID().toString();
        String valueId2 = UUID.randomUUID().toString();

        var var1 = new VariantCreateRequestDTO("BARCODE1", BigDecimal.TEN, 5, null, Map.of(optionId, valueId1));
        var var2 = new VariantCreateRequestDTO("BARCODE2", BigDecimal.TEN, 5, null, Map.of(optionId, valueId2));
        var request = new ProductCreateRequestDTO("T-Shirt", "Desc", "Brand", "Cat", null, List.of(var1, var2));

        when(productVariantRepository.findExistingBarcodes(anyList())).thenReturn(Collections.emptyList());

        when(variantOptionRepository.findAllById(anyList())).thenReturn(List.of(
                VariantOption.builder().id(UUID.fromString(optionId)).name("Color").build()
        ));
        when(variantOptionValueRepository.findAllById(anyList())).thenReturn(List.of(
                VariantOptionValue.builder().id(UUID.fromString(valueId1)).build(),
                VariantOptionValue.builder().id(UUID.fromString(valueId2)).build()
        ));

        Product savedProduct = Product.builder().id(UUID.randomUUID()).build();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toResponse(any(Product.class))).thenReturn(ProductResponseDTO.builder().build());

        // Act
        productService.createProduct(request);

        // Assert
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertEquals(2, capturedProduct.getVariants().size());
    }

    @Test
    @DisplayName("Should throw MultipleValidationException if barcode already exists in DB")
    void createProduct_WhenBarcodeExists_ShouldThrowException() {
        // Arrange
        var var1 = new VariantCreateRequestDTO("EXISTING_BARCODE", BigDecimal.TEN, 5, null, Map.of());
        var request = new ProductCreateRequestDTO("T-Shirt", "Desc", "Brand", "Cat", null, List.of(var1));

        when(productVariantRepository.findExistingBarcodes(List.of("EXISTING_BARCODE")))
                .thenReturn(List.of("EXISTING_BARCODE"));

        // Act & Assert
        MultipleValidationException exception = assertThrows(
                MultipleValidationException.class,
                () -> productService.createProduct(request)
        );

        assertEquals(1, exception.getItems().size());
        assertEquals("variants[0].barcode", exception.getItems().get(0).subject());
        assertEquals("validation.variant.barcode.exists", exception.getItems().get(0).messageKey());

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException if a VariantOption ID from payload is not in DB")
    void createProduct_WhenVariantOptionNotFound_ShouldThrowException() {
        // Arrange
        String fakeOptionId = UUID.randomUUID().toString();
        var var1 = new VariantCreateRequestDTO("BARCODE1", BigDecimal.TEN, 5, null, Map.of(fakeOptionId, UUID.randomUUID().toString()));
        var var2 = new VariantCreateRequestDTO("BARCODE2", BigDecimal.TEN, 5, null, Map.of(fakeOptionId, UUID.randomUUID().toString()));
        var request = new ProductCreateRequestDTO("T-Shirt", "Desc", "Brand", "Cat", null, List.of(var1, var2));

        when(productVariantRepository.findExistingBarcodes(anyList())).thenReturn(Collections.emptyList());

        when(variantOptionRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.createProduct(request));
        assertEquals("variantOptions", exception.getSubject());
        assertEquals("exception.not_found.variantOptions", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw MultipleValidationException if duplicate attribute combinations exist")
    void createProduct_WhenDuplicateAttributesExist_ShouldThrowException() {
        // Arrange
        String optionId = UUID.randomUUID().toString();
        String valueId = UUID.randomUUID().toString();

        var var1 = new VariantCreateRequestDTO("BARCODE1", BigDecimal.TEN, 5, null, Map.of(optionId, valueId));
        var var2 = new VariantCreateRequestDTO("BARCODE2", BigDecimal.TEN, 5, null, Map.of(optionId, valueId));
        var request = new ProductCreateRequestDTO("T-Shirt", "Desc", "Brand", "Cat", null, List.of(var1, var2));

        when(productVariantRepository.findExistingBarcodes(anyList())).thenReturn(Collections.emptyList());
        when(variantOptionRepository.findAllById(anyList())).thenReturn(List.of(VariantOption.builder().id(UUID.fromString(optionId)).build()));
        when(variantOptionValueRepository.findAllById(anyList())).thenReturn(List.of(VariantOptionValue.builder().id(UUID.fromString(valueId)).build()));

        // Act & Assert
        MultipleValidationException exception = assertThrows(
                MultipleValidationException.class,
                () -> productService.createProduct(request)
        );

        assertTrue(exception.getItems().stream().anyMatch(e -> e.messageKey().equals("validation.variant.attributes.duplicate")));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw MultipleValidationException when a variant is missing keys compared to the superset")
    void createProduct_WhenVariantHasMissingKeys_ShouldThrowException() {
        // Arrange
        String colorOptionId = UUID.randomUUID().toString();
        String sizeOptionId = UUID.randomUUID().toString();
        String valueId = UUID.randomUUID().toString();

        var var1 = new VariantCreateRequestDTO("BARCODE1", BigDecimal.TEN, 5, null, Map.of(colorOptionId, valueId, sizeOptionId, valueId));
        var var2 = new VariantCreateRequestDTO("BARCODE2", BigDecimal.TEN, 5, null, Map.of(colorOptionId, valueId));
        var request = new ProductCreateRequestDTO("T-Shirt", "Desc", "Brand", "Cat", null, List.of(var1, var2));

        when(productVariantRepository.findExistingBarcodes(anyList())).thenReturn(Collections.emptyList());

        when(variantOptionRepository.findAllById(anyList())).thenReturn(List.of(
                VariantOption.builder().id(UUID.fromString(colorOptionId)).name("Color").build(),
                VariantOption.builder().id(UUID.fromString(sizeOptionId)).name("Size").build()
        ));
        when(variantOptionValueRepository.findAllById(anyList())).thenReturn(List.of(VariantOptionValue.builder().id(UUID.fromString(valueId)).build()));

        // Act & Assert
        MultipleValidationException exception = assertThrows(MultipleValidationException.class, () -> productService.createProduct(request));

        var validationItem = exception.getItems().stream()
                .filter(i -> i.messageKey().equals("validation.variant.attributes.missing_keys"))
                .findFirst()
                .orElseThrow();

        assertEquals("variants[1].attributes", validationItem.subject());
        assertTrue(validationItem.args()[0].toString().contains("Size"));
    }
}