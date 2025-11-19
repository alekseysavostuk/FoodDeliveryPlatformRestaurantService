package v1.foodDeliveryPlatform.service.impl;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import v1.foodDeliveryPlatform.exception.ImageUploadException;
import v1.foodDeliveryPlatform.model.DishImage;
import v1.foodDeliveryPlatform.props.MinioProperties;
import v1.foodDeliveryPlatform.service.MinioService;

import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String upload(DishImage image) {
        log.info("Starting image upload process");

        try {
            log.debug("Checking/Creating bucket: {}", minioProperties.getBucket());
            createBucket();
            log.debug("Bucket check/creation completed successfully");
        } catch (Exception e) {
            log.error("Bucket operation failed for bucket: {}", minioProperties.getBucket(), e);
            throw new ImageUploadException("Image upload failed: " + e.getMessage());
        }

        MultipartFile file = image.getFile();
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            log.warn("Image upload failed - file is empty or has no name");
            throw new ImageUploadException("Image must have name.");
        }

        log.debug("Processing file: {} (size: {} bytes)",
                file.getOriginalFilename(), file.getSize());

        String fileName = generateFileName(file);
        log.debug("Generated file name: {}", fileName);

        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
            log.trace("File input stream obtained successfully");
        } catch (Exception e) {
            log.error("Failed to get input stream for file: {}", file.getOriginalFilename(), e);
            throw new ImageUploadException("Image upload failed: " + e.getMessage());
        }

        saveImage(inputStream, fileName);
        log.info("Image uploaded successfully: {}", fileName);

        return fileName;
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        log.info("Deleting file from MinIO: {}", fileName);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(fileName)
                            .build()
            );
            log.info("File deleted successfully: {}", fileName);
        } catch (Exception e) {
            log.error("Failed to delete file: {} from bucket: {}",
                    fileName, minioProperties.getBucket(), e);
            throw e;
        }
    }

    @SneakyThrows
    private void createBucket() {
        log.trace("Checking if bucket exists: {}", minioProperties.getBucket());
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());

        if (!found) {
            log.info("Bucket not found, creating new bucket: {}", minioProperties.getBucket());
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
            log.info("Bucket created successfully: {}", minioProperties.getBucket());
        } else {
            log.trace("Bucket already exists: {}", minioProperties.getBucket());
        }
    }

    private String generateFileName(final MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(file);
        String fileName = UUID.randomUUID() + "." + extension;

        log.trace("Generated file name: {} for original: {}", fileName, originalFilename);
        return fileName;
    }

    private String getExtension(final MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            log.warn("Original filename is null, using default extension");
            return "jpg";
        }

        try {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            log.trace("Extracted extension: {} from file: {}", extension, originalFilename);
            return extension;
        } catch (Exception e) {
            log.warn("Failed to extract extension from: {}, using default", originalFilename);
            return "jpg";
        }
    }

    @SneakyThrows
    private void saveImage(final InputStream inputStream, final String fileName) {
        log.debug("Saving image to MinIO: {}", fileName);

        try {
            int availableBytes = inputStream.available();
            log.trace("File size: {} bytes", availableBytes);

            minioClient.putObject(PutObjectArgs.builder()
                    .stream(inputStream, availableBytes, -1)
                    .bucket(minioProperties.getBucket())
                    .object(fileName)
                    .build());

            log.debug("Image saved successfully to MinIO: {} ({} bytes)", fileName, availableBytes);
        } catch (Exception e) {
            log.error("Failed to save image to MinIO: {}", fileName, e);
            throw e;
        } finally {
            try {
                inputStream.close();
                log.trace("Input stream closed for file: {}", fileName);
            } catch (Exception e) {
                log.warn("Failed to close input stream for file: {}", fileName, e);
            }
        }
    }
}