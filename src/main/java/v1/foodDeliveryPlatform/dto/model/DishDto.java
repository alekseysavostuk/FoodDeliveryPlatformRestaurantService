package v1.foodDeliveryPlatform.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import v1.foodDeliveryPlatform.dto.validation.OnCreate;
import v1.foodDeliveryPlatform.dto.validation.OnUpdate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dish data transfer object")
public class DishDto {

    @NotNull(message = "Id must be not null",
            groups = OnUpdate.class)
    @Schema(
            description = "Unique dish identifier (required only for updates)",
            example = "123e4567-e89b-12d3-a456-426614174000",
            format = "uuid"
    )
    private UUID id;

    @NotBlank(message = "Dish name must be not blank",
            groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255, message = "Dish name must be smaller 255 characters",
            groups = {OnCreate.class, OnUpdate.class})
    @Schema(
            description = "Name of the dish",
            example = "Margherita Pizza",
            maxLength = 255
    )
    private String name;

    @Length(max = 255, message = "Description must be smaller 255 characters",
            groups = {OnCreate.class, OnUpdate.class})
    @Schema(
            description = "Description of the dish",
            example = "Classic pizza with tomato sauce, mozzarella, and fresh basil",
            maxLength = 255
    )
    private String description;

    @NotNull(message = "Price must be not null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    @Schema(
            description = "Price of the dish",
            example = "12.99",
            minimum = "0.01",
            maximum = "9999999999.99"
    )
    private BigDecimal price;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "List of dish image URLs (read only)",
            example = "[\"https://bucket.s3.amazonaws.com/dishes/123e4567/image1.jpg\", \"https://bucket.s3.amazonaws.com/dishes/123e4567/image2.jpg\"]",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<String> images;

}
