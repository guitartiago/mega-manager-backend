package com.megamanager.consumo.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumoJpaRepository extends JpaRepository<ConsumoEntity, Long> {

    List<ConsumoEntity> findByClienteId(Long clienteId);
}
