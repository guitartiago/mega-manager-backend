package com.megamanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.megamanager.auth.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthFilter jwtFilter;

  public SecurityConfig(JwtAuthFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // 🔓 Rotas públicas (inclui Swagger/OpenAPI)
        .requestMatchers(
          "/auth/login",
          "/v3/api-docs/**",
          "/swagger-ui/**",
          "/swagger-ui.html",
          "/actuator/health",
          "/error"
        ).permitAll()

        // 🔒 Exemplo de proteção por módulo (ajuste aos seus endpoints)
        .requestMatchers(HttpMethod.GET, "/clientes/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/clientes/**").hasAnyRole("ADMIN","USER")
        .requestMatchers("/produtos/**").hasAnyRole("ADMIN", "USER")
        .requestMatchers("/estoque/**","/entradas-estoque/**").hasAnyRole("ADMIN","USER")
        .requestMatchers("/consumos/**").hasAnyRole("ADMIN","USER")

        // fallback
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
