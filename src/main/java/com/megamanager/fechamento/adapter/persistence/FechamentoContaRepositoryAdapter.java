package com.megamanager.fechamento.adapter.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.megamanager.fechamento.adapter.persistence.entity.FechamentoContaEntity;
import com.megamanager.fechamento.adapter.persistence.mapper.FechamentoMapper;
import com.megamanager.fechamento.application.port.out.FechamentoContaRepository;
import com.megamanager.fechamento.domain.FechamentoConta;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FechamentoContaRepositoryAdapter implements FechamentoContaRepository {

    private final FechamentoContaJpaRepository jpa;

    @Override
    public FechamentoConta salvar(FechamentoConta fechamento) {
        FechamentoContaEntity saved = jpa.save(FechamentoMapper.toEntity(fechamento));
        return FechamentoMapper.toDomain(saved);
    }

    @Override
    public Optional<FechamentoConta> buscarPorId(Long id) {
        return jpa.findById(id).map(FechamentoMapper::toDomain);
    }

    @Override
    public List<FechamentoConta> listar(Long clienteId, LocalDateTime de, LocalDateTime ate) {
        List<FechamentoContaEntity> ents = (clienteId != null)
                ? jpa.findByClienteIdAndDataHoraBetweenOrderByDataHoraDesc(clienteId, de, ate)
                : jpa.findByDataHoraBetweenOrderByDataHoraDesc(de, ate);

        return ents.stream().map(FechamentoMapper::toDomain).toList();
    }
}
