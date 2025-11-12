package v1.foodDeliveryPlatform.facade.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import v1.foodDeliveryPlatform.dto.minio.ImageRequest;
import v1.foodDeliveryPlatform.dto.minio.UploadUrlResponse;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.facade.ImageFacade;
import v1.foodDeliveryPlatform.mapper.DishMapper;
import v1.foodDeliveryPlatform.service.ImageService;
import v1.foodDeliveryPlatform.service.MinioService;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ImageFacadeImpl implements ImageFacade {

    private final MinioService minioService;
    private final ImageService imageService;
    private final DishMapper mapper;

    @Override
    public UploadUrlResponse getUploadUlr(UUID dishId, String fileName) throws Exception {
        String uniqueFileName = minioService.generateFileName(dishId, fileName);
        String uploadUrl = minioService.generateUploadUrl(uniqueFileName);
        return UploadUrlResponse.builder()
                .fileName(uniqueFileName)
                .uploadUrl(uploadUrl)
                .build();
    }

    @Override
    public DishDto setImagesByDishId(UUID dishId, ImageRequest images) {
        return mapper.toDto(imageService.setImagesByDishId(dishId, images.getImages()));
    }

    @Override
    public List<String> getAllByDishId(UUID dishId) {
        return imageService.getAllByDishId(dishId);
    }

    @Override
    public DishDto removeImageByDishId(UUID dishId, String image) throws Exception {
        return mapper.toDto(imageService.removeImageByDishId(dishId, image));
    }

    @Override
    public DishDto removeAllImagesByDishId(UUID dishId) {
        return mapper.toDto(imageService.removeAllImagesByDishId(dishId));
    }

}
