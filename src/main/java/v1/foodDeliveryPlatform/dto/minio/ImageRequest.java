package v1.foodDeliveryPlatform.dto.minio;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import v1.foodDeliveryPlatform.dto.validation.OnUpdate;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for associating images with a dish")
public class ImageRequest {

    @NotNull(message = "Image must be not null",
            groups = OnUpdate.class)
    @Schema(
            description = "List of image URLs to associate with the dish",
            example = "[\"https://bucket.s3.amazonaws.com/images/dish1.jpg\", \"https://bucket.s3.amazonaws.com/images/dish2.jpg\"]"
    )
    private List<String> images;
}
