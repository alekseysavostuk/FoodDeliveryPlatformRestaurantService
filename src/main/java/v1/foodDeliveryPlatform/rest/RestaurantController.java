package v1.foodDeliveryPlatform.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.dto.model.RestaurantDto;
import v1.foodDeliveryPlatform.dto.validation.OnCreate;
import v1.foodDeliveryPlatform.dto.validation.OnUpdate;
import v1.foodDeliveryPlatform.facade.DishFacade;
import v1.foodDeliveryPlatform.facade.RestaurantFacade;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@AllArgsConstructor
@Tag(
        name = "Restaurant Controller",
        description = "Restaurant API"
)
public class RestaurantController {

    private final RestaurantFacade restaurantFacade;
    private final DishFacade dishFacade;


    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by id")
    @PreAuthorize("permitAll()")
    public ResponseEntity<RestaurantDto> getById(
            @PathVariable final UUID id) {
        return new ResponseEntity<>(restaurantFacade.getById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant by id")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteById(
            @PathVariable final UUID id) {
        restaurantFacade.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Create restaurant")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<RestaurantDto> createRestaurant(
            @Validated(OnCreate.class)
            @RequestBody RestaurantDto restaurantDto) {
        return new ResponseEntity<>(restaurantFacade.createRestaurant(restaurantDto), HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Update restaurant")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<RestaurantDto> updateRestaurant(
            @Validated(OnUpdate.class)
            @RequestBody RestaurantDto restaurantDto) {
        return new ResponseEntity<>(restaurantFacade.updateRestaurant(restaurantDto), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all restaurants")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<RestaurantDto>> getAll() {
        return new ResponseEntity<>(restaurantFacade.getAllRestaurants(), HttpStatus.OK);
    }

    @GetMapping("/cuisine")
    @Operation(summary = "Get restaurants by cuisine")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<RestaurantDto>> getAllByCuisine(
            @RequestParam String cuisine) {
        return new ResponseEntity<>(restaurantFacade.getAllByCuisine(cuisine), HttpStatus.OK);
    }

    @PostMapping("/{id}/dishes")
    @Operation(summary = "Add dish to restaurant")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<DishDto> createDish(
            @Validated(OnCreate.class)
            @PathVariable final UUID id,
            @RequestBody DishDto dishDto) {
        return new ResponseEntity<>(dishFacade.createDish(dishDto, id), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/dishes")
    @Operation(summary = "Get dishes by restaurant id")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<DishDto>> getDishesByRestaurantId(
            @PathVariable final UUID id) {
        return new ResponseEntity<>(dishFacade.getAllByRestaurantId(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if a restaurant exists")
    @PreAuthorize("isAuthenticated()")
    public boolean existsRestaurant(@PathVariable final UUID id) {
        return restaurantFacade.existsRestaurant(id);
    }

    @GetMapping("/{restaurantId}/dishes/{dishId}/exists")
    @Operation(summary = "Check if dish exists")
    @PreAuthorize("isAuthenticated()")
    public boolean existsDish(@PathVariable final UUID restaurantId,
                              @PathVariable final UUID dishId) {
        return dishFacade.existsDish(restaurantId, dishId);
    }
}
