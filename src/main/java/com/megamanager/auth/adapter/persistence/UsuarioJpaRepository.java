package com.megamanager.auth.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
  Optional<UsuarioEntity> findByUsername(String username);
}
