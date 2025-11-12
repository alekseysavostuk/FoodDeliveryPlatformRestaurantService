package v1.foodDeliveryPlatform.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import v1.foodDeliveryPlatform.dto.minio.ImageRequest;
import v1.foodDeliveryPlatform.dto.minio.UploadUrlResponse;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.dto.validation.OnUpdate;
import v1.foodDeliveryPlatform.facade.ImageFacade;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dishes/{dishId}/images")
@Tag(
        name = "Dish images Controller",
        description = "Dish images API"
)
public class ImageController {

    private final ImageFacade imageFacade;

    @GetMapping("/upload-url")
    @Operation(summary = "Get upload URL")
    public ResponseEntity<UploadUrlResponse> getUploadUrl(
            @PathVariable final UUID dishId,
            @RequestParam String fileName) throws Exception {

        return new ResponseEntity<>(imageFacade.getUploadUlr(dishId, fileName), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Set dish images by dish id")
    public ResponseEntity<DishDto> setImages(
            @PathVariable final UUID dishId,
            @Validated(OnUpdate.class)
            @RequestBody ImageRequest images) {

        return new ResponseEntity<>(imageFacade.setImagesByDishId(dishId, images), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get dish images by dish id")
    public ResponseEntity<List<String>> getImages(
            @PathVariable final UUID dishId) {

        return new ResponseEntity<>(imageFacade.getAllByDishId(dishId), HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Remove specific image by dish id")
    public ResponseEntity<DishDto> removeImage(
            @PathVariable final UUID dishId,
            @RequestParam String image) throws Exception {

        return new ResponseEntity<>(imageFacade.removeImageByDishId(dishId, image), HttpStatus.OK);
    }

    @DeleteMapping("/all")
    @Operation(summary = "Remove all images by dish id")
    public ResponseEntity<DishDto> removeAllImages(
            @PathVariable final UUID dishId) {

        return new ResponseEntity<>(imageFacade.removeAllImagesByDishId(dishId), HttpStatus.OK);
    }
}
