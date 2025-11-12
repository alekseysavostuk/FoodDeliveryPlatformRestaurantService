package v1.foodDeliveryPlatform.mapper;

import org.mapstruct.Mapper;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.model.Dish;

@Mapper(componentModel = "spring", uses = {RestaurantMapper.class})
public interface DishMapper extends BaseMapper<Dish, DishDto> {

}

