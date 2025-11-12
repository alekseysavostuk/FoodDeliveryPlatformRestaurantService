package v1.foodDeliveryPlatform.mapper;

import org.mapstruct.Mapper;
import v1.foodDeliveryPlatform.dto.minio.DishImageDto;
import v1.foodDeliveryPlatform.model.DishImage;

@Mapper(componentModel = "spring")
public interface TaskImageMapper extends BaseMapper<DishImage, DishImageDto> {
}
