package v1.foodDeliveryPlatform.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import v1.foodDeliveryPlatform.config.ControllerTestSecurityConfig;
import v1.foodDeliveryPlatform.dto.minio.DishImageDto;
import v1.foodDeliveryPlatform.dto.model.DishDto;
import v1.foodDeliveryPlatform.dto.model.feign.DishClientDto;
import v1.foodDeliveryPlatform.facade.DishFacade;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DishController.class)
@Import({ControllerTestSecurityConfig.class, AdviceController.class})
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishFacade dishFacade;

    private final UUID dishId = UUID.randomUUID();
    private final UUID restaurantId = UUID.randomUUID();

    private final String createDishJson = """
        {
            "id": "%s",
            "name": "Test Dish",
            "description": "Test dish description",
            "price": 15.99,
            "restaurantId": "%s"
        }
        """.formatted(dishId, restaurantId);

    private final String updateDishJson = """
        {
            "id": "%s",
            "name": "Updated Dish",
            "description": "Updated dish description", 
            "price": 19.99,
            "restaurantId": "%s"
        }
        """.formatted(dishId, restaurantId);

    @Test
    @WithMockUser
    void getById_Success() throws Exception {
        DishDto dishDto = new DishDto();
        when(dishFacade.getById(dishId)).thenReturn(dishDto);

        mockMvc.perform(get("/api/v1/dishes/{id}", dishId))
                .andExpect(status().isOk());

        verify(dishFacade).getById(dishId);
    }

    @Test
    void getDishName_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/dishes/{id}/name", dishId))
                .andExpect(status().isForbidden());

        verify(dishFacade, never()).getNameById(any(UUID.class));
    }

    @Test
    @WithMockUser
    void getDishName_Authorized() throws Exception {
        DishClientDto clientDto = new DishClientDto();
        when(dishFacade.getNameById(dishId)).thenReturn(clientDto);

        mockMvc.perform(get("/api/v1/dishes/{id}/name", dishId))
                .andExpect(status().isOk());

        verify(dishFacade).getNameById(dishId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void deleteById_WithUserRole_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/dishes/{id}", dishId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(dishFacade, never()).delete(any(UUID.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteById_WithAdminRole_Success() throws Exception {
        doNothing().when(dishFacade).delete(dishId);

        mockMvc.perform(delete("/api/v1/dishes/{id}", dishId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(dishFacade).delete(dishId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_MANAGER"})
    void deleteById_WithManagerRole_Success() throws Exception {
        doNothing().when(dishFacade).delete(dishId);

        mockMvc.perform(delete("/api/v1/dishes/{id}", dishId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(dishFacade).delete(dishId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void updateDish_WithUserRole_Forbidden() throws Exception {
        mockMvc.perform(put("/api/v1/dishes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDishJson))
                .andExpect(status().isForbidden());

        verify(dishFacade, never()).updateDish(any(DishDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateDish_WithAdminRole_Success() throws Exception {
        DishDto dishDto = new DishDto();
        when(dishFacade.updateDish(any(DishDto.class))).thenReturn(dishDto);

        mockMvc.perform(put("/api/v1/dishes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDishJson))
                .andExpect(status().isOk());

        verify(dishFacade).updateDish(any(DishDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_MANAGER"})
    void updateDish_WithManagerRole_Success() throws Exception {
        DishDto dishDto = new DishDto();
        when(dishFacade.updateDish(any(DishDto.class))).thenReturn(dishDto);

        mockMvc.perform(put("/api/v1/dishes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDishJson))
                .andExpect(status().isOk());

        verify(dishFacade).updateDish(any(DishDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void uploadImage_WithUserRole_Forbidden() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/dishes/{id}/image", dishId)
                        .file(imageFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());

        verify(dishFacade, never()).uploadImage(any(UUID.class), any(DishImageDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void uploadImage_WithAdminRole_Success() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        DishDto dishDto = new DishDto();
        when(dishFacade.uploadImage(eq(dishId), any(DishImageDto.class))).thenReturn(dishDto);

        mockMvc.perform(multipart("/api/v1/dishes/{id}/image", dishId)
                        .file(imageFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(dishFacade).uploadImage(eq(dishId), any(DishImageDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_MANAGER"})
    void uploadImage_WithManagerRole_Success() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        DishDto dishDto = new DishDto();
        when(dishFacade.uploadImage(eq(dishId), any(DishImageDto.class))).thenReturn(dishDto);

        mockMvc.perform(multipart("/api/v1/dishes/{id}/image", dishId)
                        .file(imageFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(dishFacade).uploadImage(eq(dishId), any(DishImageDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void uploadImage_WithoutFile_BadRequest() throws Exception {

        mockMvc.perform(multipart("/api/v1/dishes/{id}/image", dishId)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        verify(dishFacade, never()).uploadImage(any(UUID.class), any(DishImageDto.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void uploadImage_WithInvalidFile_BadRequest() throws Exception {

        MockMultipartFile emptyFile = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/dishes/{id}/image", dishId)
                        .file(emptyFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        verify(dishFacade, never()).uploadImage(any(UUID.class), any(DishImageDto.class));
    }
}
