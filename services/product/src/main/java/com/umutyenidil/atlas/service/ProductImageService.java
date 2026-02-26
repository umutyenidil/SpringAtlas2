package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.response.ProductImageUploadResponseDTO;
import com.umutyenidil.atlas.enumeration.StorageProvider;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {

    StorageProvider getStorageProvider();

    ProductImageUploadResponseDTO upload(MultipartFile file);
}
