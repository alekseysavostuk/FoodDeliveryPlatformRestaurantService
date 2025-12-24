package v1.foodDeliveryPlatform.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.facade.DishFacade;
import v1.foodDeliveryPlatform.facade.ImageFacade;
import v1.foodDeliveryPlatform.service.MinioService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dishes/{dishId}/images")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowCredentials = "true"
)
@Tag(
        name = "Dish images Controller",
        description = "Dish images API"
)
public class ImageDishController {

    private final ImageFacade imageFacade;
    private final DishFacade dishFacade;
    private final MinioService minioService;

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

    @GetMapping("/{imageName:.+}")
    @Operation(summary = "Get dish image by name")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> getImage(
            @PathVariable final UUID dishId,
            @PathVariable final String imageName,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) throws Exception {

        if (!dishFacade.getById(dishId).getImages().contains(imageName)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] imageData = minioService.getFile(imageName);
        String tag = DigestUtils.md5DigestAsHex(imageData);

        if (ifNoneMatch != null && ifNoneMatch.equals("\"" + tag + "\"")) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, getContentType(imageName))
                .header(HttpHeaders.ETAG, "\"" + tag + "\"")
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .body(imageData);
    }

    private String getContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
}
