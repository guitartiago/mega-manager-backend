package com.megamanager.auth.adapter.persistence;

import java.util.Set;

import com.megamanager.auth.domain.Usuario;

public final class UsuarioMapper {
  private UsuarioMapper() {}
  public static Usuario toDomain(UsuarioEntity e) {
    return new Usuario(e.getId(), e.getUsername(), e.getSenhaHash(),
        e.getPerfis() == null ? Set.of() : Set.copyOf(e.getPerfis()), e.isAtivo());
  }
}
