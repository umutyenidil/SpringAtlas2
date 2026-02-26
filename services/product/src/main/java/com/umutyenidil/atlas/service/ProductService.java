package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.request.ProductCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO createProduct(ProductCreateRequestDTO request);
}
