package v1.foodDeliveryPlatform.dto.model.feign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Restaurant feign client data transfer object")
public class RestaurantClientDto {

    @NotBlank(message = "Restaurant name must be not blank")
    @Length(max = 255, message = "Restaurant name must be smaller 255 characters")
    @Schema(
            description = "Restaurant name",
            maxLength = 255
    )
    private String restaurantName;
}
