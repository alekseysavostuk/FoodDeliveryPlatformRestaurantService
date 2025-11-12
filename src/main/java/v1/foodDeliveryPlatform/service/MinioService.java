package v1.foodDeliveryPlatform.service;

import v1.foodDeliveryPlatform.model.DishImage;


public interface MinioService {

    void deleteFile(String fileName) throws Exception;

    String upload(DishImage image);
}
