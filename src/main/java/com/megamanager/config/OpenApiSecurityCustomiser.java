package com.megamanager.config;
// com.megamanager.config.OpenApiSecurityCustomiser
import java.util.Set;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class OpenApiSecurityCustomiser {
  private static final Set<String> PUBLIC = Set.of(
      "/auth/", "/v3/api-docs", "/swagger-ui", "/actuator/health", "/error"
  );

  @Bean
  OpenApiCustomizer secureEverythingExceptPublic() {
    return openApi -> {
      var req = new SecurityRequirement().addList("bearerAuth");
      openApi.getPaths().forEach((path, item) -> {
        boolean isPublic = PUBLIC.stream().anyMatch(path::startsWith)
                           || "/swagger-ui.html".equals(path);
        if (!isPublic) item.readOperations().forEach(op -> op.addSecurityItem(req));
      });
    };
  }
}
