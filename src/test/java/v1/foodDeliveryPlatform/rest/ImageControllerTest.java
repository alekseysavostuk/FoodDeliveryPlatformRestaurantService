package v1.foodDeliveryPlatform.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import v1.foodDeliveryPlatform.config.ControllerTestSecurityConfig;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.facade.ImageFacade;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageDishController.class)
@Import({ControllerTestSecurityConfig.class, AdviceController.class})
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageFacade imageFacade;

    private final UUID dishId = UUID.randomUUID();
    private final String imageName = "test-image.jpg";

    @Test
    @WithMockUser(roles = "ADMIN")
    void getImages_Success() throws Exception {
        List<String> images = List.of("image1.jpg", "image2.jpg");
        when(imageFacade.getAllByDishId(dishId)).thenReturn(images);

        mockMvc.perform(get("/api/v1/dishes/{dishId}/images", dishId))
                .andExpect(status().isOk());

        verify(imageFacade).getAllByDishId(dishId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getImages_WhenNoImages_ReturnsEmptyList() throws Exception {
        when(imageFacade.getAllByDishId(dishId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/dishes/{dishId}/images", dishId))
                .andExpect(status().isOk());

        verify(imageFacade).getAllByDishId(dishId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getImages_WhenDishNotFound_ReturnsEmptyList() throws Exception {
        UUID nonExistentDishId = UUID.randomUUID();
        when(imageFacade.getAllByDishId(nonExistentDishId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/dishes/{dishId}/images", nonExistentDishId))
                .andExpect(status().isOk());

        verify(imageFacade).getAllByDishId(nonExistentDishId);
    }

    @Test
    void removeImage_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images", dishId)
                        .param("image", imageName)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(imageFacade, never()).removeImageByDishId(any(UUID.class), any(String.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void removeImage_WithUserRole_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images", dishId)
                        .param("image", imageName)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(imageFacade, never()).removeImageByDishId(any(UUID.class), any(String.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void removeImage_WithAdminRole_Success() throws Exception {
        DishDto dishDto = new DishDto();
        when(imageFacade.removeImageByDishId(dishId, imageName)).thenReturn(dishDto);

        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images", dishId)
                        .param("image", imageName)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(imageFacade).removeImageByDishId(dishId, imageName);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_MANAGER"})
    void removeImage_WithManagerRole_Success() throws Exception {
        DishDto dishDto = new DishDto();
        when(imageFacade.removeImageByDishId(dishId, imageName)).thenReturn(dishDto);

        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images", dishId)
                        .param("image", imageName)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(imageFacade).removeImageByDishId(dishId, imageName);
    }

    @Test
    void removeAllImages_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images/all", dishId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(imageFacade, never()).removeAllImagesByDishId(any(UUID.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void removeAllImages_WithUserRole_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images/all", dishId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(imageFacade, never()).removeAllImagesByDishId(any(UUID.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void removeAllImages_WithAdminRole_Success() throws Exception {
        DishDto dishDto = new DishDto();
        when(imageFacade.removeAllImagesByDishId(dishId)).thenReturn(dishDto);

        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images/all", dishId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(imageFacade).removeAllImagesByDishId(dishId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_MANAGER"})
    void removeAllImages_WithManagerRole_Success() throws Exception {
        DishDto dishDto = new DishDto();
        when(imageFacade.removeAllImagesByDishId(dishId)).thenReturn(dishDto);

        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images/all", dishId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(imageFacade).removeAllImagesByDishId(dishId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void removeImage_WithoutImageParam_BadRequest() throws Exception {

        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images", dishId)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(imageFacade, never()).removeImageByDishId(any(UUID.class), any(String.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void removeImage_WithEmptyImageParam_Success() throws Exception {

        DishDto dishDto = new DishDto();
        when(imageFacade.removeImageByDishId(dishId, "")).thenReturn(dishDto);

        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images", dishId)
                        .param("image", "")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(imageFacade).removeImageByDishId(dishId, "");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void removeImage_WhenImageNotFound_ReturnsOk() throws Exception {
        when(imageFacade.removeImageByDishId(dishId, imageName))
                .thenThrow(new RuntimeException("Image not found"));

        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images", dishId)
                        .param("image", imageName)
                        .with(csrf()))
                .andExpect(status().is5xxServerError());

        verify(imageFacade).removeImageByDishId(dishId, imageName);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void removeAllImages_WhenDishNotFound_ReturnsOk() throws Exception {
        when(imageFacade.removeAllImagesByDishId(dishId))
                .thenThrow(new RuntimeException("Dish not found"));

        mockMvc.perform(delete("/api/v1/dishes/{dishId}/images/all", dishId)
                        .with(csrf()))
                .andExpect(status().is5xxServerError());

        verify(imageFacade).removeAllImagesByDishId(dishId);
    }
}