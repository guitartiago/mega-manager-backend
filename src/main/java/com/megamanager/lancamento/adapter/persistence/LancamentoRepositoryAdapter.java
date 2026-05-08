package com.megamanager.lancamento.adapter.persistence;

import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.Lancamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LancamentoRepositoryAdapter implements LancamentoRepository {

    private final LancamentoJpaRepository jpa;

    @Override
    public Lancamento salvar(Lancamento lancamento) {
        LancamentoEntity entity = LancamentoMapper.toEntity(lancamento);
        LancamentoEntity salvo = jpa.save(entity);
        return LancamentoMapper.toDomain(salvo);
    }

    @Override
    public List<Lancamento> buscarPorCliente(Long clienteId) {
        return jpa.findByClienteIdOrderByDataHoraDesc(clienteId)
                .stream()
                .map(LancamentoMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Lancamento> buscarPorId(Long id) {
        return jpa.findById(id)
                .map(LancamentoMapper::toDomain);
    }

    @Override
    public boolean existeEstornoParaOrigem(Long lancamentoOrigemId) {
        return jpa.existsByLancamentoOrigemId(lancamentoOrigemId);
    }

    @Override
    public List<Lancamento> buscarNaoProcessadosPorCliente(Long clienteId) {
        return jpa.findByClienteIdAndFechamentoIdIsNullOrderByDataHoraAsc(clienteId)
                .stream()
                .map(LancamentoMapper::toDomain)
                .toList();
    }

    @Override
    public void marcarLancamentosComoProcessados(List<Long> lancamentoIds, Long fechamentoId) {
        LocalDateTime agora = LocalDateTime.now();
        jpa.updateFechamentoIdBatch(lancamentoIds, fechamentoId, agora);
    }
}
