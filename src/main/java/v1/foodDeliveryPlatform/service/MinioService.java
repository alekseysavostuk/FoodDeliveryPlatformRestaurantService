package v1.foodDeliveryPlatform.service;

import v1.foodDeliveryPlatform.model.ModelImage;


public interface MinioService {

    void deleteFile(String fileName) throws Exception;

    String upload(ModelImage image);

    byte[] getFile(String fileName) throws Exception;
}
