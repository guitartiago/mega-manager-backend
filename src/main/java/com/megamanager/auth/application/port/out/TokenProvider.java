package com.megamanager.auth.application.port.out;

import java.util.Set;

public interface TokenProvider {
  String gerar(String subject, Set<String> roles);
  boolean valido(String token);
  String subject(String token);
  Set<String> roles(String token);
  long expiresAt(String token);
}
