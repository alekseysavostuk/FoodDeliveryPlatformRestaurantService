package v1.foodDeliveryPlatform.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import v1.foodDeliveryPlatform.config.ControllerTestSecurityConfig;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.dto.model.RestaurantDto;
import v1.foodDeliveryPlatform.dto.model.feign.RestaurantClientDto;
import v1.foodDeliveryPlatform.facade.DishFacade;
import v1.foodDeliveryPlatform.facade.RestaurantFacade;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
@Import({ControllerTestSecurityConfig.class, AdviceController.class})
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantFacade restaurantFacade;

    @MockitoBean
    private DishFacade dishFacade;

    private final UUID restaurantId = UUID.randomUUID();
    private final UUID dishId = UUID.randomUUID();

    private final String createRestaurantJson = """
        {
            "name": "Test Restaurant",
            "cuisine": "Italian",
            "address": "123 Main Street, Warsaw"
        }
        """;

    private final String updateRestaurantJson = """
        {
            "id": "%s",
            "name": "Updated Restaurant",
            "cuisine": "Italian", 
            "address": "456 Updated Street, Warsaw"
        }
        """.formatted(restaurantId);

    private final String createDishJson = """
        {
            "name": "Test Dish",
            "description": "Test dish description",
            "price": 15.99
        }
        """;

    @Test
    void getById_Success() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        when(restaurantFacade.getById(restaurantId)).thenReturn(restaurantDto);

        mockMvc.perform(get("/api/v1/restaurants/{id}", restaurantId))
                .andExpect(status().isOk());

        verify(restaurantFacade).getById(restaurantId);
    }

    @Test
    void getAll_Success() throws Exception {
        List<RestaurantDto> restaurants = List.of(new RestaurantDto());
        when(restaurantFacade.getAllRestaurants()).thenReturn(restaurants);

        mockMvc.perform(get("/api/v1/restaurants"))
                .andExpect(status().isOk());

        verify(restaurantFacade).getAllRestaurants();
    }

    @Test
    void getAllByCuisine_Success() throws Exception {
        List<RestaurantDto> restaurants = List.of(new RestaurantDto());
        when(restaurantFacade.getAllByCuisine("Italian")).thenReturn(restaurants);

        mockMvc.perform(get("/api/v1/restaurants/cuisine")
                        .param("cuisine", "Italian"))
                .andExpect(status().isOk());

        verify(restaurantFacade).getAllByCuisine("Italian");
    }

    @Test
    void getDishesByRestaurantId_Success() throws Exception {
        List<DishDto> dishes = List.of(new DishDto());
        when(dishFacade.getAllByRestaurantId(restaurantId)).thenReturn(dishes);

        mockMvc.perform(get("/api/v1/restaurants/{id}/dishes", restaurantId))
                .andExpect(status().isOk());

        verify(dishFacade).getAllByRestaurantId(restaurantId);
    }

    @Test
    void existsRestaurant_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants/{id}/exists", restaurantId))
                .andExpect(status().isForbidden());

        verify(restaurantFacade, never()).existsRestaurant(any(UUID.class));
    }

    @Test
    @WithMockUser
    void existsRestaurant_Authorized() throws Exception {
        when(restaurantFacade.existsRestaurant(restaurantId)).thenReturn(true);

        mockMvc.perform(get("/api/v1/restaurants/{id}/exists", restaurantId))
                .andExpect(status().isOk());

        verify(restaurantFacade).existsRestaurant(restaurantId);
    }

    @Test
    void existsDish_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/dishes/{dishId}/exists", restaurantId, dishId))
                .andExpect(status().isForbidden());

        verify(dishFacade, never()).existsDish(any(UUID.class), any(UUID.class));
    }

    @Test
    @WithMockUser
    void existsDish_Authorized() throws Exception {
        when(dishFacade.existsDish(restaurantId, dishId)).thenReturn(true);

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/dishes/{dishId}/exists", restaurantId, dishId))
                .andExpect(status().isOk());

        verify(dishFacade).existsDish(restaurantId, dishId);
    }

    @Test
    void getRestaurantName_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants/{id}/name", restaurantId))
                .andExpect(status().isForbidden());

        verify(restaurantFacade, never()).getNameById(any(UUID.class));
    }

    @Test
    @WithMockUser
    void getRestaurantName_Authorized() throws Exception {
        RestaurantClientDto clientDto = new RestaurantClientDto();
        when(restaurantFacade.getNameById(restaurantId)).thenReturn(clientDto);

        mockMvc.perform(get("/api/v1/restaurants/{id}/name", restaurantId))
                .andExpect(status().isOk());

        verify(restaurantFacade).getNameById(restaurantId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void createRestaurant_WithUserRole_Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/restaurants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRestaurantJson))
                .andExpect(status().isForbidden());

        verify(restaurantFacade, never()).createRestaurant(any(RestaurantDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createRestaurant_WithAdminRole_Success() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        when(restaurantFacade.createRestaurant(any(RestaurantDto.class))).thenReturn(restaurantDto);

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRestaurantJson))
                .andExpect(status().isCreated());

        verify(restaurantFacade).createRestaurant(any(RestaurantDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void createDish_WithUserRole_Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/restaurants/{id}/dishes", restaurantId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createDishJson))
                .andExpect(status().isForbidden());

        verify(dishFacade, never()).createDish(any(DishDto.class), any(UUID.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createDish_WithAdminRole_Success() throws Exception {
        DishDto dishDto = new DishDto();
        when(dishFacade.createDish(any(DishDto.class), eq(restaurantId))).thenReturn(dishDto);

        mockMvc.perform(post("/api/v1/restaurants/{id}/dishes", restaurantId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createDishJson))
                .andExpect(status().isCreated());

        verify(dishFacade).createDish(any(DishDto.class), eq(restaurantId));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void deleteById_WithUserRole_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/restaurants/{id}", restaurantId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(restaurantFacade, never()).delete(any(UUID.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteById_WithAdminRole_Success() throws Exception {
        doNothing().when(restaurantFacade).delete(restaurantId);

        mockMvc.perform(delete("/api/v1/restaurants/{id}", restaurantId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(restaurantFacade).delete(restaurantId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void updateRestaurant_WithUserRole_Forbidden() throws Exception {
        mockMvc.perform(put("/api/v1/restaurants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRestaurantJson))
                .andExpect(status().isForbidden());

        verify(restaurantFacade, never()).updateRestaurant(any(RestaurantDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateRestaurant_WithAdminRole_Success() throws Exception {
        RestaurantDto restaurantDto = new RestaurantDto();
        when(restaurantFacade.updateRestaurant(any(RestaurantDto.class))).thenReturn(restaurantDto);

        mockMvc.perform(put("/api/v1/restaurants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRestaurantJson))
                .andExpect(status().isOk());

        verify(restaurantFacade).updateRestaurant(any(RestaurantDto.class));
    }
}