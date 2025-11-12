package v1.foodDeliveryPlatform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import v1.foodDeliveryPlatform.dto.model.RestaurantDto;
import v1.foodDeliveryPlatform.model.Restaurant;

@Mapper(componentModel = "spring")
public interface RestaurantMapper extends BaseMapper<Restaurant, RestaurantDto> {
    @Override
    @Mapping(target = "dishDtoList", source = "dishes")
    RestaurantDto toDto(Restaurant restaurant);

    @Override
    @Mapping(target = "dishes", source = "dishDtoList")
    Restaurant toEntity(RestaurantDto restaurantDto);
}
