package com.megamanager.auth.adapter.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.auth.application.port.in.AutenticarUseCase;
import com.megamanager.auth.application.port.out.TokenProvider;
import com.megamanager.auth.application.port.out.UsuarioRepository;
import com.megamanager.auth.application.usecase.AutenticarService;
import com.megamanager.auth.security.JwtTokenProvider;

@Configuration
public class AuthConfig {

	@Bean
	public SecretKey jwtSecretKey(@Value("${security.jwt.secret}") String secret) {
		byte[] keyBytes;
		try {
			keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
		} catch (IllegalArgumentException e) {
			keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		}
		if (keyBytes.length < 32) {
			throw new IllegalStateException("JWT secret precisa ter >= 32 bytes (256 bits).");
		}
		return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
	}

	@Bean
	public TokenProvider tokenProvider(SecretKey jwtSecretKey,
			@Value("${security.jwt.expiration-minutes}") int expirationMinutes) {
		return new JwtTokenProvider(jwtSecretKey, expirationMinutes);
	}

	@Bean
    public AutenticarUseCase autenticarUseCase(UsuarioRepository usuarioRepository,
                                               TokenProvider tokenProvider) {
        return new AutenticarService(usuarioRepository, tokenProvider);
    }
}
