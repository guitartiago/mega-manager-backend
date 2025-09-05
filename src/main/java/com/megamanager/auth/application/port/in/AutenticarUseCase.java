package com.megamanager.auth.application.port.in;

public interface AutenticarUseCase {
  TokenDTO autenticar(String username, String senha);
  record TokenDTO(String token, long expiresAt) {}
}
