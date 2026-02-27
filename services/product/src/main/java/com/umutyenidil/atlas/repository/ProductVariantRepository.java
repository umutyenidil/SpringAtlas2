package com.umutyenidil.atlas.repository;

import com.umutyenidil.atlas.entity.ProductVariant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    @Query("SELECT v.barcode FROM ProductVariant  v WHERE v.barcode IN :barcodes")
    List<String> findExistingBarcodes(@Param("barcodes") List<String> barcodes);

    @EntityGraph(attributePaths = {"product"})
    Optional<ProductVariant> findWithProductById(UUID id);
}
