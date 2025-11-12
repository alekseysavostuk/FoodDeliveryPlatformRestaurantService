package v1.foodDeliveryPlatform.facade;

import v1.foodDeliveryPlatform.dto.minio.ImageRequest;
import v1.foodDeliveryPlatform.dto.minio.UploadUrlResponse;
import v1.foodDeliveryPlatform.dto.model.DishDto;

import java.util.List;
import java.util.UUID;

public interface ImageFacade {
    UploadUrlResponse getUploadUlr(UUID dishId, String fileName) throws Exception;

    DishDto setImagesByDishId(UUID dishId, ImageRequest images);

    List<String> getAllByDishId(UUID dishId);

    DishDto removeImageByDishId(UUID dishId, String image) throws Exception;

    DishDto removeAllImagesByDishId(UUID dishId);
}
