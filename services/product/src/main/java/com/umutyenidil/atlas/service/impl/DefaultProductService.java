package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.request.ProductCreateRequestDTO;
import com.umutyenidil.atlas.dto.request.VariantCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.ProductResponseDTO;
import com.umutyenidil.atlas.entity.*;
import com.umutyenidil.atlas.exception.MultipleValidationException;
import com.umutyenidil.atlas.exception.NotFoundException;
import com.umutyenidil.atlas.repository.ProductRepository;
import com.umutyenidil.atlas.repository.ProductVariantRepository;
import com.umutyenidil.atlas.repository.VariantOptionRepository;
import com.umutyenidil.atlas.repository.VariantOptionValueRepository;
import com.umutyenidil.atlas.service.ProductMapper;
import com.umutyenidil.atlas.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMapper productMapper;
    private final VariantOptionRepository variantOptionRepository;
    private final VariantOptionValueRepository variantOptionValueRepository;

    public DefaultProductService(ProductRepository productRepository, ProductVariantRepository productVariantRepository, ProductMapper productMapper, VariantOptionRepository variantOptionRepository, VariantOptionValueRepository variantOptionValueRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productMapper = productMapper;
        this.variantOptionRepository = variantOptionRepository;
        this.variantOptionValueRepository = variantOptionValueRepository;
    }

    @Transactional
    @Override
    public ProductResponseDTO createProduct(ProductCreateRequestDTO request) {
        validate(request);

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .brand(request.brand())
                .category(request.category())
                .images(new ArrayList<>())
                .variants(new ArrayList<>())
                .build();

        if (request.images() != null && !request.images().isEmpty()) {
            List<ProductImage> images = request.images().stream()
                    .map(r -> ProductImage.builder()
                            .fileKey(r.fileKey())
                            .provider(r.provider())
                            .product(product)
                            .build())
                    .toList();

            product.getImages().addAll(images);
        }

        if (request.variants() != null) {
            List<ProductVariant> variants = request.variants().stream()
                    .map(v -> {
                        ProductVariant variant = ProductVariant.builder()
                                .barcode(v.barcode())
                                .price(v.price())
                                .stockQuantity(v.stockQuantity())
                                .product(product)
                                .images(new ArrayList<>())
                                .build();

                        if (v.images() != null && !v.images().isEmpty()) {
                            List<ProductImage> images = v.images().stream()
                                    .map(i -> ProductImage.builder()
                                            .fileKey(i.fileKey())
                                            .provider(i.provider())
                                            .product(product)
                                            .variant(variant)
                                            .build())
                                    .toList();

                            variant.getImages().addAll(images);
                        }

                        return variant;
                    })
                    .toList();

            product.getVariants().addAll(variants);
        }

        Product savedProduct = productRepository.save(product);
        product.setId(savedProduct.getId());

        return productMapper.toResponse(product);
    }

    private void validate(ProductCreateRequestDTO request) {
        validateVariantBarcodes(request);

        validateVariantAttributeMaps(request);
    }

    private void validateVariantAttributeMaps(ProductCreateRequestDTO request) {
        if (request.variants() != null && request.variants().size() > 1) {
            Set<String> allOptionIds = extractAllOptionIds(request.variants());
            List<VariantOption> allOptions = variantOptionRepository.findAllById(allOptionIds.stream().map(UUID::fromString).toList());
            List<VariantCreateRequestDTO> variantCreateRequests = request.variants();
            List<Map<String, String>> allAttributes = variantCreateRequests.stream().map(VariantCreateRequestDTO::attributes).toList();
            Set<String> variantOptionValueIds = allAttributes.stream().flatMap(m->m.values().stream()).collect(Collectors.toSet());
            List<VariantOptionValue> allVariantOptionValues = variantOptionValueRepository.findAllById(variantOptionValueIds.stream().map(UUID::fromString).toList());

            if (allOptions.size() != allOptionIds.size()) {
                throw new NotFoundException("variantOptions", "exception.not_found.variantOptions");
            }

            if (variantOptionValueIds.size() != allVariantOptionValues.size()) {
                throw new NotFoundException("variantOptions", "exception.not_found.variantOptionValues");
            }

            List<MultipleValidationException.Item> exceptionItems = new ArrayList<>();
            Map<Map<String, String>, Integer> seenAttributes = new HashMap<>();

            for (int i = 0; i < request.variants().size(); i++) {
                var variant = request.variants().get(i);
                var attributes = variant.attributes();

                if (attributes == null || attributes.isEmpty()) {
                    exceptionItems.add(new MultipleValidationException.Item(
                            "variants[" + i + "].attributes",
                            "validation.variant.attributes.required"
                    ));
                    continue;
                }

                Set<String> optionIds = attributes.keySet();
                if (!optionIds.equals(allOptionIds)) {
                    Set<String> missingOptionIds = new HashSet<>(allOptionIds);
                    missingOptionIds.removeAll(optionIds);
                    Set<String> missingOptionNames = variantOptionRepository.findAllById(missingOptionIds.stream().map(UUID::fromString).toList()).stream().map(VariantOption::getName).collect(Collectors.toSet());

                    exceptionItems.add(new MultipleValidationException.Item(
                            "variants[" + i + "].attributes",
                            "validation.variant.attributes.missing_keys",
                            String.join(", ", missingOptionNames)
                    ));
                    continue;
                }

                if (seenAttributes.containsKey(attributes)) {
                    exceptionItems.add(new MultipleValidationException.Item(
                            "variants[" + i + "].attributes",
                            "validation.variant.attributes.duplicate"
                    ));
                } else {
                    seenAttributes.put(attributes, i);
                }
            }

            if (!exceptionItems.isEmpty()) {
                throw new MultipleValidationException(exceptionItems);
            }
        }
    }

    private Set<String> extractAllOptionIds(List<VariantCreateRequestDTO> variants) {
        Set<String> allOptionIds = new HashSet<>();

        for (var variant : variants) {
            if (variant.attributes() != null) {
                allOptionIds.addAll(variant.attributes().keySet());
            }
        }

        return allOptionIds;
    }

    private void validateVariantBarcodes(ProductCreateRequestDTO request) {
        if (request.variants() == null || request.variants().isEmpty()) {
            return;
        }

        List<String> incomingBarcodes = request.variants().stream()
                .map(VariantCreateRequestDTO::barcode)
                .toList();

        List<String> existingBarcodes = productVariantRepository.findExistingBarcodes(incomingBarcodes);

        if (!existingBarcodes.isEmpty()) {
            List<MultipleValidationException.Item> exceptionItems = new ArrayList<>();

            for (int i = 0; i < request.variants().size(); i++) {
                String currentBarcode = request.variants().get(i).barcode();

                if (existingBarcodes.contains(currentBarcode)) {
                    exceptionItems.add(new MultipleValidationException.Item(
                            "variants[" + i + "].barcode",
                            "validation.variant.barcode.exists"
                    ));
                }
            }

            throw new MultipleValidationException(exceptionItems);
        }
    }
}
