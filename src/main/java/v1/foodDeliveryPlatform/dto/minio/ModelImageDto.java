package v1.foodDeliveryPlatform.dto.minio;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dish Image DTO for file upload")
public class ModelImageDto {

    @NotNull(message = "Image must be not null")
    @Schema(
            description = "Image file to upload",
            format = "binary"
    )
    private MultipartFile file;
}
