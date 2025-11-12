package v1.foodDeliveryPlatform.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DishImage {

    private MultipartFile file;
}
