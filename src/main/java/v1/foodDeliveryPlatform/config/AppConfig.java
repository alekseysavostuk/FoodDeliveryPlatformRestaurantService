package v1.foodDeliveryPlatform.config;

import io.minio.MinioClient;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import v1.foodDeliveryPlatform.props.MinioProperties;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class AppConfig {

    private final MinioProperties minioProperties;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Service API")
                        .description("Food delivery platform")
                        .version("1.0.0")
                );
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
}
