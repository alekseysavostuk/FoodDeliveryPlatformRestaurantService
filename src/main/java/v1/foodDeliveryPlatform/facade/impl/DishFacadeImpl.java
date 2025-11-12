package v1.foodDeliveryPlatform.facade.impl;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import v1.foodDeliveryPlatform.dto.minio.DishImageDto;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.facade.DishFacade;
import v1.foodDeliveryPlatform.mapper.DishMapper;
import v1.foodDeliveryPlatform.mapper.TaskImageMapper;
import v1.foodDeliveryPlatform.model.Dish;
import v1.foodDeliveryPlatform.service.DishService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DishFacadeImpl implements DishFacade {

    private final DishService dishService;
    private final DishMapper mapper;
    private final TaskImageMapper taskImageMapper;

    @Override
    public DishDto getById(UUID id) {
        return mapper.toDto(dishService.getById(id));
    }

    @Override
    public DishDto createDish(DishDto dishDto, UUID restaurantId) {
        return mapper.toDto(dishService.createDish(mapper.toEntity(dishDto), restaurantId));
    }

    @Override
    public List<DishDto> getAllByRestaurantId(UUID restaurantId) {
        List<Dish> dishes = dishService.getAllByRestaurantId(restaurantId);
        return dishes.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public DishDto updateDish(DishDto dishDto) {
        return mapper.toDto(dishService.updateDish(mapper.toEntity(dishDto)));
    }

    @Override
    public void delete(UUID id) {
        dishService.delete(id);
    }

    @Override
    public DishDto uploadImage(UUID id, DishImageDto image) {
        return mapper.toDto(dishService.uploadImage(id, taskImageMapper.toEntity(image)));
    }
}
