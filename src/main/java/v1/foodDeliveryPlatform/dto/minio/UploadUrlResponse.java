package v1.foodDeliveryPlatform.dto.minio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing pre-signed URL for image upload")
public class UploadUrlResponse {

    @Schema(
            description = "Name of the file to be uploaded",
            example = "dish-image.jpg"
    )
    private String fileName;

    @Schema(
            description = "Pre-signed URL for uploading the file to cloud storage",
            example = "https://example-bucket.s3.amazonaws.com/uploads/123e4567/dish-image.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&..."
    )
    private String uploadUrl;
}
