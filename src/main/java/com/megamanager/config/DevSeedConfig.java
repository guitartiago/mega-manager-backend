// com.megamanager.config.DevSeedConfig.java
package com.megamanager.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.megamanager.auth.adapter.persistence.UsuarioEntity;
import com.megamanager.auth.adapter.persistence.UsuarioJpaRepository;

@Profile("h2")
@Configuration
public class DevSeedConfig {

	@Bean
	CommandLineRunner seedAdmin(UsuarioJpaRepository jpa, PasswordEncoder encoder) {
	  return args -> {
	    jpa.findByUsername("admin").orElseGet(() -> {
	      var u = new UsuarioEntity();
	      u.setUsername("admin");
	      u.setSenhaHash(encoder.encode("admin123"));
	      u.setPerfis(Set.of("ROLE_ADMIN","ROLE_USER"));
	      u.setAtivo(true);
	      return jpa.save(u);
	    });
	  };
	}
}
