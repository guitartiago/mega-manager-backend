package com.megamanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SecurityScheme(
  name = "bearerAuth",
  type = SecuritySchemeType.HTTP,
  scheme = "bearer",
  bearerFormat = "JWT"
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI megaManagerOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("MegaManager API")
                .description("Sistema para controle de produtos, estoque e ensaios no estúdio MegaFuzz")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Tiago Correia")
                    .email("guitartiago@gmail.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
            );
    }
}
