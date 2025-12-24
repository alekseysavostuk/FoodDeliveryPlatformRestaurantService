package v1.foodDeliveryPlatform.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import v1.foodDeliveryPlatform.dto.minio.ModelImageDto;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.dto.model.feign.DishClientDto;
import v1.foodDeliveryPlatform.dto.validation.OnUpdate;
import v1.foodDeliveryPlatform.facade.DishFacade;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dishes")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowCredentials = "true"
)
@AllArgsConstructor
@Tag(
        name = "Dish Controller",
        description = "Dish API"
)
public class DishController {

    private final DishFacade dishFacade;

    @GetMapping("/{id}")
    @Operation(summary = "Get dish by id")
    @PreAuthorize("permitAll()")
    public ResponseEntity<DishDto> getById(
            @PathVariable final UUID id) {
        return new ResponseEntity<>(dishFacade.getById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dish by id")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteById(
            @PathVariable final UUID id) {
        dishFacade.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Update dish")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<DishDto> updateDish(
            @Validated(OnUpdate.class)
            @RequestBody DishDto dishDto) {
        return new ResponseEntity<>(dishFacade.updateDish(dishDto), HttpStatus.OK);
    }

    @PostMapping("/{id}/image")
    @Operation(summary = "Upload image to dish")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<DishDto> uploadImage(
            @PathVariable final UUID id,
            @Validated @ModelAttribute final ModelImageDto imageDto
    ) {
        return new ResponseEntity<>(dishFacade.uploadImage(id, imageDto), HttpStatus.OK);
    }

    @GetMapping("/{id}/name")
    @Operation(summary = "Get dish name")
    @PreAuthorize("isAuthenticated()")
    public DishClientDto getDishName(@PathVariable final UUID id) {
        return dishFacade.getNameById(id);
    }
}
