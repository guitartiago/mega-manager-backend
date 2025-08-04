package com.megamanager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.megamanager.persistence.entity.ClienteEntity;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
}