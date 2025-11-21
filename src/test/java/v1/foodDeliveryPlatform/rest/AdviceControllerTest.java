package v1.foodDeliveryPlatform.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import v1.foodDeliveryPlatform.exception.ExceptionBody;
import v1.foodDeliveryPlatform.exception.ImageUploadException;
import v1.foodDeliveryPlatform.exception.ResourceNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdviceControllerTest {

    private AdviceController adviceController;

    @BeforeEach
    void setUp() {
        adviceController = new AdviceController();
    }

    @Test
    void handleResourceNotFound_ShouldReturnNotFoundStatus() {
        String errorMessage = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        ExceptionBody result = adviceController.handleResourceNotFound(exception);

        assertNotNull(result);
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void handleIllegalState_ShouldReturnBadRequestStatus() {
        String errorMessage = "Invalid operation state";
        IllegalStateException exception = new IllegalStateException(errorMessage);

        ExceptionBody result = adviceController.handleIllegalState(exception);

        assertNotNull(result);
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequestWithValidationErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("dishDto", "name", "Name must not be empty");
        FieldError fieldError2 = new FieldError("dishDto", "price", "Price must be positive");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ExceptionBody result = adviceController.handleMethodArgumentNotValid(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertEquals(2, result.getErrors().size());
        assertEquals("Name must not be empty", result.getErrors().get("name"));
        assertEquals("Price must be positive", result.getErrors().get("price"));
    }

    @Test
    void handleMethodArgumentNotValid_DuplicateFieldErrors_ShouldConcatenateMessages() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("dishDto", "name", "Name must not be empty");
        FieldError fieldError2 = new FieldError("dishDto", "name", "Name must be at least 2 characters");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ExceptionBody result = adviceController.handleMethodArgumentNotValid(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Name must not be empty Name must be at least 2 characters", result.getErrors().get("name"));
    }

    @Test
    void handleConstraintViolation_ShouldReturnBadRequestWithValidationErrors() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();

        ConstraintViolation<?> violation1 = createConstraintViolation("dish.name", "Name must not be empty");
        ConstraintViolation<?> violation2 = createConstraintViolation("dish.price", "Price must be positive");

        violations.add(violation1);
        violations.add(violation2);

        ConstraintViolationException exception = new ConstraintViolationException(violations);

        ExceptionBody result = adviceController.handleConstraintViolation(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertEquals(2, result.getErrors().size());
        assertEquals("Name must not be empty", result.getErrors().get("dish.name"));
        assertEquals("Price must be positive", result.getErrors().get("dish.price"));
    }

    @Test
    void handleAccessDenied_ShouldReturnForbiddenStatus() {
        String errorMessage = "Access denied message";
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        ExceptionBody result = adviceController.handleAccessDenied(exception);

        assertNotNull(result);
        assertEquals("Access denied: " + errorMessage, result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void handleMissingServletRequestParameter_ShouldReturnBadRequestStatus() {
        String parameterName = "image";
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException(parameterName, "String");

        ExceptionBody result = adviceController.handleMissingServletRequestParameter(exception);

        assertNotNull(result);
        assertEquals("Required parameter 'image' is missing", result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void handleAuthenticationCredentialsNotFound_ShouldReturnUnauthorizedStatus() {
        String errorMessage = "Authentication credentials not found";
        AuthenticationCredentialsNotFoundException exception =
                new AuthenticationCredentialsNotFoundException(errorMessage);

        ExceptionBody result = adviceController.handleAuthenticationException(exception);

        assertNotNull(result);
        assertEquals("Authentication required: " + errorMessage, result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void handleImageUpload_ShouldReturnBadRequestStatus() {
        String errorMessage = "Failed to upload image";
        ImageUploadException exception = new ImageUploadException(errorMessage);

        ExceptionBody result = adviceController.handleImageUpload(exception);

        assertNotNull(result);
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void handleException_GenericException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Database connection failed");

        ExceptionBody result = adviceController.handleException(exception);

        assertNotNull(result);
        assertEquals("Internal error", result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void handleException_RuntimeException_ShouldReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("Unexpected runtime error");

        ExceptionBody result = adviceController.handleException(exception);

        assertNotNull(result);
        assertEquals("Internal error", result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void handleMethodArgumentNotValid_EmptyFieldErrors_ShouldReturnEmptyErrorsMap() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ExceptionBody result = adviceController.handleMethodArgumentNotValid(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void handleConstraintViolation_EmptyViolations_ShouldReturnEmptyErrorsMap() {
        ConstraintViolationException exception = new ConstraintViolationException(Set.of());

        ExceptionBody result = adviceController.handleConstraintViolation(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void exceptionBody_ConstructorAndGetters_ShouldWorkCorrectly() {
        String message = "Test error message";

        ExceptionBody exceptionBody = new ExceptionBody(message);

        assertNotNull(exceptionBody);
        assertEquals(message, exceptionBody.getMessage());
        assertNull(exceptionBody.getErrors());
    }

    @Test
    void exceptionBody_Setters_ShouldWorkCorrectly() {
        ExceptionBody exceptionBody = new ExceptionBody("Initial message");

        exceptionBody.setMessage("Updated message");
        exceptionBody.setErrors(Map.of("field", "error message"));

        assertEquals("Updated message", exceptionBody.getMessage());
        assertNotNull(exceptionBody.getErrors());
        assertEquals("error message", exceptionBody.getErrors().get("field"));
    }

    @Test
    void handleAccessDenied_MultipleCalls_ShouldReturnConsistentResponse() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        ExceptionBody result1 = adviceController.handleAccessDenied(exception);
        ExceptionBody result2 = adviceController.handleAccessDenied(exception);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("Access denied: Access denied", result1.getMessage());
        assertEquals("Access denied: Access denied", result2.getMessage());
        assertNull(result1.getErrors());
        assertNull(result2.getErrors());
    }

    @Test
    void handleMissingServletRequestParameter_DifferentParameters_ShouldFormatCorrectly() {
        MissingServletRequestParameterException exception1 =
                new MissingServletRequestParameterException("id", "UUID");
        MissingServletRequestParameterException exception2 =
                new MissingServletRequestParameterException("name", "String");

        ExceptionBody result1 = adviceController.handleMissingServletRequestParameter(exception1);
        ExceptionBody result2 = adviceController.handleMissingServletRequestParameter(exception2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("Required parameter 'id' is missing", result1.getMessage());
        assertEquals("Required parameter 'name' is missing", result2.getMessage());
    }

    @Test
    void handleImageUpload_DifferentMessages_ShouldReturnCorrectMessage() {
        ImageUploadException exception1 = new ImageUploadException("File too large");
        ImageUploadException exception2 = new ImageUploadException("Invalid file format");

        ExceptionBody result1 = adviceController.handleImageUpload(exception1);
        ExceptionBody result2 = adviceController.handleImageUpload(exception2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("File too large", result1.getMessage());
        assertEquals("Invalid file format", result2.getMessage());
    }

    @Test
    void handleMethodArgumentNotValid_MultipleDuplicateFields_ShouldConcatenateAllMessages() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("dishDto", "name", "Name must not be empty");
        FieldError fieldError2 = new FieldError("dishDto", "name", "Name must be at least 2 characters");
        FieldError fieldError3 = new FieldError("dishDto", "name", "Name must not exceed 50 characters");
        FieldError fieldError4 = new FieldError("dishDto", "price", "Price must be positive");
        FieldError fieldError5 = new FieldError("dishDto", "price", "Price must not exceed 1000");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2, fieldError3, fieldError4, fieldError5));

        ExceptionBody result = adviceController.handleMethodArgumentNotValid(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertEquals(2, result.getErrors().size());
        assertEquals("Name must not be empty Name must be at least 2 characters Name must not exceed 50 characters",
                result.getErrors().get("name"));
        assertEquals("Price must be positive Price must not exceed 1000",
                result.getErrors().get("price"));
    }

    @Test
    void handleConstraintViolation_ComplexPropertyPaths_ShouldHandleCorrectly() {
        ConstraintViolationException exception = getConstraintViolationException();

        ExceptionBody result = adviceController.handleConstraintViolation(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertEquals(4, result.getErrors().size());
        assertEquals("Name error", result.getErrors().get("dish.name"));
        assertEquals("Restaurant ID error", result.getErrors().get("dish.restaurant.id"));
        assertEquals("Quantity error", result.getErrors().get("items[0].quantity"));
        assertEquals("Street error", result.getErrors().get("user.address.street"));
    }

    @NotNull
    private ConstraintViolationException getConstraintViolationException() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();

        ConstraintViolation<?> violation1 = createConstraintViolation("dish.name", "Name error");
        ConstraintViolation<?> violation2 = createConstraintViolation("dish.restaurant.id", "Restaurant ID error");
        ConstraintViolation<?> violation3 = createConstraintViolation("items[0].quantity", "Quantity error");
        ConstraintViolation<?> violation4 = createConstraintViolation("user.address.street", "Street error");

        violations.add(violation1);
        violations.add(violation2);
        violations.add(violation3);
        violations.add(violation4);

        return new ConstraintViolationException(violations);
    }

    @Test
    void handleMethodArgumentNotValid_SingleFieldError_ShouldReturnSingleError() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dishDto", "description", "Description is required");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ExceptionBody result = adviceController.handleMethodArgumentNotValid(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Description is required", result.getErrors().get("description"));
    }

    @Test
    void handleConstraintViolation_SingleViolation_ShouldReturnSingleError() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();

        ConstraintViolation<?> violation = createConstraintViolation("dish.price", "Price must be positive");

        violations.add(violation);

        ConstraintViolationException exception = new ConstraintViolationException(violations);

        ExceptionBody result = adviceController.handleConstraintViolation(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.getMessage());
        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Price must be positive", result.getErrors().get("dish.price"));
    }

    private ConstraintViolation<?> createConstraintViolation(String propertyPath, String message) {
        return new ConstraintViolation<>() {
            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public Object getRootBean() {
                return null;
            }

            @Override
            public Class<Object> getRootBeanClass() {
                return Object.class;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return new Path() {
                    @Override
                    public String toString() {
                        return propertyPath;
                    }

                    @Override
                    public Iterator<Node> iterator() {
                        return null;
                    }
                };
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> type) {
                return null;
            }
        };
    }
}