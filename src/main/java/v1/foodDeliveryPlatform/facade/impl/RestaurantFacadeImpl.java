package v1.foodDeliveryPlatform.facade.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import v1.foodDeliveryPlatform.dto.minio.ModelImageDto;
import v1.foodDeliveryPlatform.dto.model.RestaurantDto;
import v1.foodDeliveryPlatform.dto.model.feign.RestaurantClientDto;
import v1.foodDeliveryPlatform.facade.RestaurantFacade;
import v1.foodDeliveryPlatform.mapper.RestaurantClientMapper;
import v1.foodDeliveryPlatform.mapper.RestaurantMapper;
import v1.foodDeliveryPlatform.mapper.TaskImageMapper;
import v1.foodDeliveryPlatform.model.Restaurant;
import v1.foodDeliveryPlatform.service.RestaurantService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RestaurantFacadeImpl implements RestaurantFacade {

    private final RestaurantService restaurantService;
    private final RestaurantMapper mapper;
    private final RestaurantClientMapper restaurantClientMapper;
    private final TaskImageMapper taskImageMapper;

    @Override
    public RestaurantDto getById(UUID id) {
        return mapper.toDto(restaurantService.getById(id));
    }

    @Override
    public RestaurantDto createRestaurant(RestaurantDto restaurantDto) {
        return mapper.toDto(restaurantService.createRestaurant(mapper.toEntity(restaurantDto)));
    }

    @Override
    public List<RestaurantDto> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return restaurants.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public RestaurantDto updateRestaurant(RestaurantDto restaurantDto) {
        return mapper.toDto(restaurantService.updateRestaurant(mapper.toEntity(restaurantDto)));
    }

    @Override
    public void delete(UUID id) {
        restaurantService.delete(id);
    }

    @Override
    public List<RestaurantDto> getAllByCuisine(String cuisine) {
        List<Restaurant> restaurants = restaurantService.getAllByCuisine(cuisine);
        return restaurants.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public boolean existsRestaurant(UUID id) {
        return restaurantService.existsRestaurant(id);
    }

    @Override
    public RestaurantClientDto getNameById(UUID id) {
        return restaurantClientMapper.toDto(restaurantService.getNameById(id));
    }

    @Override
    public RestaurantDto uploadImage(UUID id, ModelImageDto image) {
        return mapper.toDto(restaurantService.uploadImage(id, taskImageMapper.toEntity(image)));
    }
}
