package v1.foodDeliveryPlatform.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import v1.foodDeliveryPlatform.dto.validation.OnCreate;
import v1.foodDeliveryPlatform.dto.validation.OnUpdate;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Restaurant data transfer object")
public class RestaurantDto {

    @NotNull(message = "Id must be not null",
            groups = OnUpdate.class)
    @Schema(
            description = "Unique restaurant identifier (required only for updates)",
            example = "123e4567-e89b-12d3-a456-426614174000",
            format = "uuid"
    )
    private UUID id;

    @NotBlank(message = "Restaurant name must be not blank",
            groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255, message = "Restaurant name must be smaller 255 characters",
            groups = {OnCreate.class, OnUpdate.class})
    @Schema(
            description = "Name of the restaurant",
            example = "La Bella Italia",
            maxLength = 255
    )
    private String name;

    @NotBlank(message = "Cuisine must be not blank",
            groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255, message = "Cuisine must be smaller 255 characters",
            groups = {OnCreate.class, OnUpdate.class})
    @Schema(
            description = "Type of cuisine served at the restaurant",
            example = "Italian",
            maxLength = 255
    )
    private String cuisine;

    @NotBlank(message = "Address must be not blank",
            groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255, message = "Address must be smaller 255 characters",
            groups = {OnCreate.class, OnUpdate.class})
    @Schema(
            description = "Physical address of the restaurant",
            example = "123 Main Street, New York, NY 10001",
            maxLength = 255
    )
    private String address;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "List of dishes available at the restaurant (read only)",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<DishDto> dishDtoList;
}
