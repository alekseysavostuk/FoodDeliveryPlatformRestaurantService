package v1.foodDeliveryPlatform.service;

import io.minio.*;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import v1.foodDeliveryPlatform.exception.ImageUploadException;
import v1.foodDeliveryPlatform.model.DishImage;
import v1.foodDeliveryPlatform.props.MinioProperties;
import v1.foodDeliveryPlatform.service.impl.MinioServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private InputStream inputStream;

    @InjectMocks
    private MinioServiceImpl minioService;

    private final String testBucketName = "test-bucket";
    private final String testFileName = "test-image.jpg";

    @Test
    void upload_Success() throws Exception {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);

        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(testFileName);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(1024);

        String result = minioService.upload(dishImage);

        assertNotNull(result);
        assertTrue(result.endsWith(".jpg"));
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
    }

    @Test
    void upload_BucketCreationSuccess() throws Exception {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(testFileName);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(1024);

        String result = minioService.upload(dishImage);

        assertNotNull(result);

        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void upload_BucketOperationFailed() throws Exception {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenThrow(new RuntimeException("Bucket error"));

        ImageUploadException exception = assertThrows(ImageUploadException.class,
                () -> minioService.upload(dishImage));

        assertTrue(exception.getMessage().contains("Image upload failed"));
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(multipartFile, never()).isEmpty();
    }

    @Test
    void upload_EmptyFile() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(true);

        ImageUploadException exception = assertThrows(ImageUploadException.class,
                () -> minioService.upload(dishImage));

        assertEquals("Image must have name.", exception.getMessage());
        verify(multipartFile).isEmpty();
        verify(multipartFile, never()).getInputStream();
    }

    @Test
    void upload_NullFilename() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        ImageUploadException exception = assertThrows(ImageUploadException.class,
                () -> minioService.upload(dishImage));

        assertEquals("Image must have name.", exception.getMessage());
        verify(multipartFile).getOriginalFilename();
        verify(multipartFile, never()).getInputStream();
    }

    @Test
    void upload_InputStreamException() throws Exception {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(testFileName);
        when(multipartFile.getInputStream()).thenThrow(new RuntimeException("Stream error"));

        ImageUploadException exception = assertThrows(ImageUploadException.class,
                () -> minioService.upload(dishImage));

        assertTrue(exception.getMessage().contains("Image upload failed"));
        verify(multipartFile).getInputStream();
        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }

    @Test
    void upload_SaveImageException() throws Exception {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(testFileName);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(1024);

        doThrow(new RuntimeException("Save error")).when(minioClient).putObject(any(PutObjectArgs.class));

        assertThrows(RuntimeException.class, () -> minioService.upload(dishImage));

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void deleteFile_Success() throws Exception {
        when(minioProperties.getBucket()).thenReturn(testBucketName);

        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertDoesNotThrow(() -> minioService.deleteFile(testFileName));

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteFile_Exception() throws Exception {
        when(minioProperties.getBucket()).thenReturn(testBucketName);

        doThrow(new RuntimeException("Delete error")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        Exception exception = assertThrows(Exception.class,
                () -> minioService.deleteFile(testFileName));

        assertEquals("Delete error", exception.getMessage());
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void upload_ExtensionExtractionSuccess() throws Exception {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("image.png");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(1024);

        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenAnswer(invocation -> null);

        String result = minioService.upload(dishImage);

        assertNotNull(result);
        assertTrue(result.endsWith(".png"));
    }

    @Test
    void upload_ExtensionExtractionFallback() throws Exception {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("image");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(1024);

        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenAnswer(invocation -> null);

        String result = minioService.upload(dishImage);

        assertNotNull(result);

        assertTrue(result.matches("[a-f0-9-]+\\..+"),
                "File name should contain UUID and extension: " + result);

        assertTrue(result.contains("."), "File name should contain extension: " + result);
    }

    @Test
    void upload_MakeBucketException() throws Exception {
        DishImage dishImage = createTestDishImage();
        when(minioProperties.getBucket()).thenReturn(testBucketName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        doThrow(new RuntimeException("Make bucket error")).when(minioClient).makeBucket(any(MakeBucketArgs.class));

        ImageUploadException exception = assertThrows(ImageUploadException.class,
                () -> minioService.upload(dishImage));

        assertTrue(exception.getMessage().contains("Image upload failed"));
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }

    private DishImage createTestDishImage() {
        DishImage dishImage = new DishImage();
        dishImage.setFile(multipartFile);
        return dishImage;
    }
}