package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.response.ProductImageUploadResponseDTO;
import com.umutyenidil.atlas.enumeration.StorageProvider;
import com.umutyenidil.atlas.exception.InternalServerException;
import com.umutyenidil.atlas.exception.ValidationException;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ProductImageServiceTest {

    @Mock
    private S3Template s3Template;

    @InjectMocks
    private S3ProductImageService s3ProductImageService;

    private final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3ProductImageService, "bucketName", BUCKET_NAME);
    }

    @Test
    @DisplayName("getStorageProvider() should return StorageProvider.S3")
    public void getStorageProvider_ShouldReturnS3() {
        // Act
        StorageProvider provider = s3ProductImageService.getStorageProvider();

        // Assert
        assertEquals(StorageProvider.S3, provider);
    }

    @Test
    @DisplayName("upload() should throw ValidationException when the file is empty")
    public void upload_WhenFileIsEmpty_ShouldThrowValidationException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> s3ProductImageService.upload(emptyFile)
        );

        assertEquals("file", exception.getSubject());
        assertEquals("exception.upload.file_empty", exception.getMessageKey());

        verify(s3Template, never()).upload(anyString(), anyString(), any(InputStream.class));
    }

    @Test
    @DisplayName("upload() should successfully upload file and return DTO with generated UUID key")
    public void upload_WhenFileIsValid_ShouldUploadAndReturnResponse() throws Exception {
        // Arrange
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "product-image.jpg",
                "image/jpeg",
                "dummy-image-content".getBytes()
        );

        S3Resource mockResource = mock(S3Resource.class);
        URL mockUrl = new URL("https://s3.aws.com/" + BUCKET_NAME + "/generated-uuid.jpg");

        when(s3Template.upload(eq(BUCKET_NAME), anyString(), any(InputStream.class))).thenReturn(mockResource);
        when(mockResource.getURL()).thenReturn(mockUrl);

        // Act
        ProductImageUploadResponseDTO response = s3ProductImageService.upload(validFile);

        // Assert
        assertNotNull(response);
        assertEquals(StorageProvider.S3, response.provider());
        assertEquals(mockUrl.toString(), response.url());
        assertTrue(response.cdnUri().startsWith("s3://" + BUCKET_NAME + "/"));
        assertTrue(response.fileKey().endsWith(".jpg"));

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(s3Template, times(1)).upload(eq(BUCKET_NAME), keyCaptor.capture(), any(InputStream.class));
        assertEquals(response.fileKey(), keyCaptor.getValue());
    }

    @Test
    @DisplayName("upload() should handle files without extensions seamlessly")
    public void upload_WhenFileHasNoExtension_ShouldGenerateKeyWithoutExtension() throws Exception {
        // Arrange
        MockMultipartFile fileWithoutExtension = new MockMultipartFile(
                "file",
                "fileWithoutExtension",
                "application/octet-stream",
                "content".getBytes()
        );

        S3Resource mockResource = mock(S3Resource.class);
        when(s3Template.upload(eq(BUCKET_NAME), anyString(), any(InputStream.class))).thenReturn(mockResource);
        when(mockResource.getURL()).thenReturn(new URL("https://s3.aws.com/dummy"));

        // Act
        ProductImageUploadResponseDTO response = s3ProductImageService.upload(fileWithoutExtension);

        // Assert
        assertNotNull(response);
        assertFalse(response.fileKey().contains("."));
    }

    @Test
    @DisplayName("upload() should throw InternalServerException when IOException occurs during file read")
    public void upload_WhenIOExceptionOccurs_ShouldThrowInternalServerException() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("error-image.png");

        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated stream failure"));

        // Act & Assert
        InternalServerException exception = assertThrows(
                InternalServerException.class,
                () -> s3ProductImageService.upload(mockFile)
        );

        assertEquals("file", exception.getSubject());
        assertEquals("exception.upload.storage_failed", exception.getMessageKey());

        verify(s3Template, never()).upload(anyString(), anyString(), any(InputStream.class));
    }
}