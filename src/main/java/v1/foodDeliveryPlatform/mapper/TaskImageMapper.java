package v1.foodDeliveryPlatform.mapper;

import org.mapstruct.Mapper;
import v1.foodDeliveryPlatform.dto.minio.ModelImageDto;
import v1.foodDeliveryPlatform.model.ModelImage;

@Mapper(componentModel = "spring")
public interface TaskImageMapper extends BaseMapper<ModelImage, ModelImageDto> {
}
