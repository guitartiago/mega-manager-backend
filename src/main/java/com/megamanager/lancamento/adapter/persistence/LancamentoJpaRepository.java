package com.megamanager.lancamento.adapter.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoJpaRepository extends JpaRepository<LancamentoEntity, Long> {

    List<LancamentoEntity> findByClienteIdOrderByDataHoraDesc(Long clienteId);

    boolean existsByLancamentoOrigemId(Long lancamentoOrigemId);
}
