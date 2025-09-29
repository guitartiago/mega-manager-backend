package com.megamanager.usuario.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.megamanager.usuario.adapter.persistence.entity.UsuarioEntity;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByUsername(String username);
}
