package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.response.ProductImageUploadResponseDTO;
import com.umutyenidil.atlas.enumeration.StorageProvider;
import com.umutyenidil.atlas.exception.InternalServerException;
import com.umutyenidil.atlas.exception.ValidationException;
import com.umutyenidil.atlas.service.ProductImageService;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3ProductImageService implements ProductImageService {

    private final S3Template s3Template;

    public S3ProductImageService(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public StorageProvider getStorageProvider() {
        return StorageProvider.S3;
    }

    @Override
    public ProductImageUploadResponseDTO upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("file", "exception.upload.file_empty");
        }

        String uniqueKey = generateUniqueKey(file);

        try {
            S3Resource resource = s3Template.upload(bucketName, uniqueKey, file.getInputStream());

            return ProductImageUploadResponseDTO.builder()
                    .fileKey(uniqueKey)
                    .url(resource.getURL().toString())
                    .cdnUri("s3://" + bucketName + "/" + uniqueKey)
                    .provider(StorageProvider.S3)
                    .build();
        } catch (IOException e) {
            throw new InternalServerException("file", "exception.upload.storage_failed");
        }
    }

    private String generateUniqueKey(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }
}
