package v1.foodDeliveryPlatform.service.impl;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import v1.foodDeliveryPlatform.props.MinioProperties;
import v1.foodDeliveryPlatform.service.MinioService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .build());
                log.info("MinIO bucket '{}' created successfully", minioProperties.getBucket());
            } else {
                log.info("MinIO bucket '{}' already exists", minioProperties.getBucket());
            }
        } catch (Exception e) {
            log.warn("Failed to initialize MinIO client: {}", e.getMessage());
            log.warn("MinIO operations will be disabled. Error: {}", e.getClass().getSimpleName());
        }
    }

    @Override
    public String generateUploadUrl(String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(minioProperties.getBucket())
                        .object(fileName)
                        .expiry(60 * 60)
                        .build()
        );
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(fileName)
                        .build()
        );
    }

    @Override
    public String generateFileName(UUID dishId, String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFileName);

        return "dishes/" + dishId + "/" + timestamp + "-" + random + extension;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex > 0) ? fileName.substring(lastDotIndex) : ".jpg";
    }
}
