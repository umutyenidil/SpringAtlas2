package com.umutyenidil.atlas.repository;

import com.umutyenidil.atlas.entity.VariantOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantOptionRepository extends JpaRepository<VariantOption, UUID> {

    Optional<VariantOption> findByName(String name);
}
