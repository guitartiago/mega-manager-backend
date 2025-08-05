package com.megamanager.cliente.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
}