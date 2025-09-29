// com.megamanager.config.DevSeedConfig.java
package com.megamanager.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.megamanager.usuario.adapter.persistence.UsuarioJpaRepository;
import com.megamanager.usuario.adapter.persistence.entity.UsuarioEntity;
import com.megamanager.usuario.domain.PerfilUsuario;

@Profile("h2")
@Configuration
public class DevSeedConfig {

	@Bean
	CommandLineRunner seedAdmin(UsuarioJpaRepository jpa, PasswordEncoder encoder) {
	  return args -> {
	    jpa.findByUsername("admin").orElseGet(() -> {
	      var u = UsuarioEntity.builder().username("admin").senhaHash(encoder.encode("admin123")).perfis(Set.of(PerfilUsuario.ADMIN, PerfilUsuario.USER)).ativo(true).build();
	      return jpa.save(u);
	    });
	  };
	}
}
