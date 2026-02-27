package com.umutyenidil.atlas.entity;

import com.umutyenidil.atlas.enumeration.StorageProvider;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_images")
@Entity
public class ProductImage {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, name = "file_key")
    private String fileKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageProvider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;
}
