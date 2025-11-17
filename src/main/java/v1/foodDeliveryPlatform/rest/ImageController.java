package v1.foodDeliveryPlatform.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import v1.foodDeliveryPlatform.dto.model.DishDto;
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

    @GetMapping
    @Operation(summary = "Get dish images by dish id")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<String>> getImages(
            @PathVariable final UUID dishId) {

        return new ResponseEntity<>(imageFacade.getAllByDishId(dishId), HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Remove specific image by dish id")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<DishDto> removeImage(
            @PathVariable final UUID dishId,
            @RequestParam String image) throws Exception {

        return new ResponseEntity<>(imageFacade.removeImageByDishId(dishId, image), HttpStatus.OK);
    }

    @DeleteMapping("/all")
    @Operation(summary = "Remove all images by dish id")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<DishDto> removeAllImages(
            @PathVariable final UUID dishId) {

        return new ResponseEntity<>(imageFacade.removeAllImagesByDishId(dishId), HttpStatus.OK);
    }
}
