package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.response.ProductResponseDTO;
import com.umutyenidil.atlas.entity.Product;

public interface ProductMapper {

    ProductResponseDTO toResponse(Product product);
}
