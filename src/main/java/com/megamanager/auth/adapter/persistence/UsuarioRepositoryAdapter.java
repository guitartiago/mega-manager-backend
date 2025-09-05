package com.megamanager.auth.adapter.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.megamanager.auth.application.port.out.UsuarioRepository;
import com.megamanager.auth.domain.Usuario;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {
  private final UsuarioJpaRepository jpa;

  public UsuarioRepositoryAdapter(UsuarioJpaRepository jpa) { this.jpa = jpa; }

  @Override
  public Optional<Usuario> findByUsername(String username) {
    return jpa.findByUsername(username).map(UsuarioMapper::toDomain);
  }
}
