package com.umutyenidil.atlas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "variant_option_values")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class VariantOptionValue {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "variant_option_id")
    private VariantOption variantOption;
}
