package com.megamanager.lancamento.adapter.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.Lancamento;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LancamentoRepositoryAdapter implements LancamentoRepository {

    private final LancamentoJpaRepository jpaRepository;

    @Override
    public Lancamento salvar(Lancamento lancamento) {
        LancamentoEntity entity = LancamentoMapper.toEntity(lancamento);
        LancamentoEntity salvo = jpaRepository.save(entity);
        return LancamentoMapper.toDomain(salvo);
    }

    @Override
    public List<Lancamento> buscarPorCliente(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByDataHoraDesc(clienteId)
                .stream()
                .map(LancamentoMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Lancamento> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(LancamentoMapper::toDomain);
    }

    @Override
    public boolean existeEstornoParaOrigem(Long lancamentoOrigemId) {
        return jpaRepository.existsByLancamentoOrigemId(lancamentoOrigemId);
    }
}
