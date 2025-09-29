package com.megamanager.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.megamanager.auth.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthFilter jwtFilter;

  public SecurityConfig(JwtAuthFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }
  
  @Bean
  @Order(1)
  SecurityFilterChain h2ConsoleSecurity(HttpSecurity http) throws Exception {
      http
          .securityMatcher(new AntPathRequestMatcher("/h2-console/**"))
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .csrf(csrf -> csrf.disable())
          // H2 usa frames; precisa liberar:
          .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

      return http.build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      // ⬇️ habilita CORS usando o bean corsConfigurationSource()
      .cors(cors -> {}) 
      .authorizeHttpRequests(auth -> auth
        // libera preflight (OPTIONS) para qualquer rota
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // 🔓 públicas
        .requestMatchers(
          "/auth/login",
          "/v3/api-docs/**",
          "/swagger-ui/**",
          "/swagger-ui.html",
          "/actuator/health",
          "/error"
        ).permitAll()

        // 🔒 protegidas
        .requestMatchers(HttpMethod.GET, "/clientes/**").hasAnyRole("ADMIN","USER")
        .requestMatchers("/clientes/**").hasAnyRole("ADMIN","USER")
        .requestMatchers("/produtos/**").hasAnyRole("ADMIN","USER")
        .requestMatchers("/estoque/**","/entradas-estoque/**").hasAnyRole("ADMIN","USER")
        .requestMatchers("/consumos/**").hasAnyRole("ADMIN","USER")

        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // Use AllowedOriginPatterns quando allowCredentials = true
    config.setAllowedOriginPatterns(List.of(
      "http://localhost:4200"
      //,"https://seu-dominio-frontend.com"  // coloque aqui quando publicar
    ));
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","Origin"));
    config.setExposedHeaders(List.of("Authorization")); // se precisar ler esse header no front
    config.setAllowCredentials(true); // usa Authorization header no browser

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
