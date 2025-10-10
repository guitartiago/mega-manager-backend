package com.megamanager.fechamento.adapter.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.megamanager.fechamento.adapter.persistence.entity.FechamentoContaEntity;

public interface FechamentoContaJpaRepository extends JpaRepository<FechamentoContaEntity, Long> {
    List<FechamentoContaEntity> findByClienteIdAndDataHoraBetweenOrderByDataHoraDesc(
            Long clienteId, LocalDateTime de, LocalDateTime ate);

    List<FechamentoContaEntity> findByDataHoraBetweenOrderByDataHoraDesc(
            LocalDateTime de, LocalDateTime ate);
}
