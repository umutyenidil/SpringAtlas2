package com.umutyenidil.atlas.repository;

import com.umutyenidil.atlas.entity.VariantOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantOptionValueRepository extends JpaRepository<VariantOptionValue, UUID> {

    Optional<VariantOptionValue> findByVariantOption_IdAndName(UUID variantOptionId, String name);
}
