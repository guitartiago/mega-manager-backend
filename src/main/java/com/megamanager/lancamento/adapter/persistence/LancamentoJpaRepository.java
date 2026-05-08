package com.megamanager.lancamento.adapter.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoJpaRepository extends JpaRepository<LancamentoEntity, Long> {

    List<LancamentoEntity> findByClienteIdOrderByDataHoraDesc(Long clienteId);

    boolean existsByLancamentoOrigemId(Long lancamentoOrigemId);

    /**
     * Busca lançamentos não processados ordenados por data (mais antigos primeiro)
     */
    List<LancamentoEntity> findByClienteIdAndFechamentoIdIsNullOrderByDataHoraAsc(Long clienteId);

    /**
     * Atualiza múltiplos lançamentos em batch
     */
    @Modifying
    @Query("UPDATE LancamentoEntity l SET l.fechamentoId = :fechamentoId, l.dataProcessamento = :dataProcessamento " +
            "WHERE l.id IN :lancamentoIds")
    void updateFechamentoIdBatch(
            @Param("lancamentoIds") List<Long> lancamentoIds,
            @Param("fechamentoId") Long fechamentoId,
            @Param("dataProcessamento") LocalDateTime dataProcessamento
    );
}
