package com.megamanager.auth.application.port.out;

import java.util.Set;

import com.megamanager.usuario.domain.PerfilUsuario;

public interface TokenProvider {
  String gerar(String subject, Set<PerfilUsuario> roles);
  boolean valido(String token);
  String subject(String token);
  Set<String> roles(String token);
  long expiresAt(String token);
}
