package com.fullstack.inventario;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class InventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventario API")
                        .version("1.0.0")
                        .description("API para gestionar inventario y stock. " +
                                "IMPORTANTE: Todos los endpoints requieren el header 'X-API-Key: secret-key-inventario'")
                        .contact(new Contact()
                                .name("Fullstack Team")
                                .email("support@fullstack.local")))
                .addSecurityItem(new SecurityRequirement().addList("api-key"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("api-key", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("API Key para autenticación. Valor: secret-key-inventario")));
    }
}
