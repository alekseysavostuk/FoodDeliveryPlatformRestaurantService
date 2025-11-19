package v1.foodDeliveryPlatform.facade;

import v1.foodDeliveryPlatform.dto.minio.DishImageDto;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.dto.model.feign.DishClientDto;

import java.util.List;
import java.util.UUID;

public interface DishFacade {

    DishDto getById(UUID id);

    DishDto createDish(DishDto dishDto, UUID restaurantId);

    List<DishDto> getAllByRestaurantId(UUID restaurantId);

    DishDto updateDish(DishDto dishDto);

    void delete(UUID id);

    DishDto uploadImage(UUID id, DishImageDto image);

    boolean existsDish(UUID restaurantId, UUID dishId);

    DishClientDto getNameById(UUID id);
}
