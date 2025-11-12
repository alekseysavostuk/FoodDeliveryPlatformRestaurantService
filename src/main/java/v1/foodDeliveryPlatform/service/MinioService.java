package v1.foodDeliveryPlatform.service;

import java.util.UUID;

public interface MinioService {

    String generateUploadUrl(String fileName) throws Exception;

    void deleteFile(String fileName) throws Exception;

    String generateFileName(UUID dishId, String originalFileName);
}
