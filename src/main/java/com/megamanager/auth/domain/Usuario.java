package com.megamanager.auth.domain;

import java.util.Set;
import lombok.Getter;

@Getter
public class Usuario {
  private final Long id;
  private final String username;
  private final String senhaHash;
  private final Set<String> perfis; // ex: ["ROLE_ADMIN", "ROLE_USER"]
  private final boolean ativo;

  public Usuario(Long id, String username, String senhaHash, Set<String> perfis, boolean ativo) {
    if (username == null || username.isBlank()) throw new IllegalArgumentException("username obrigatório");
    if (senhaHash == null || senhaHash.isBlank()) throw new IllegalArgumentException("senhaHash obrigatório");
    this.id = id;
    this.username = username;
    this.senhaHash = senhaHash;
    this.perfis = perfis;
    this.ativo = ativo;
  }
}
