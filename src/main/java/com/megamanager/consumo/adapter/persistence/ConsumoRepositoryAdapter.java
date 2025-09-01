package com.megamanager.consumo.adapter.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConsumoRepositoryAdapter implements ConsumoRepository {

    private final ConsumoJpaRepository jpaRepository;

    @Override
    public Consumo salvar(Consumo consumo) {
        ConsumoEntity entity = ConsumoMapper.toEntity(consumo);
        ConsumoEntity salvo = jpaRepository.save(entity);
        return ConsumoMapper.toDomain(salvo);
    }

    @Override
    public List<Consumo> buscarPorCliente(Long clienteId) {
        return jpaRepository.findByClienteId(clienteId)
                .stream()
                .map(ConsumoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Consumo> buscarNaoPagosPorCliente(Long clienteId) {
        return jpaRepository.findByClienteIdAndPagoFalse(clienteId)
                .stream()
                .map(ConsumoMapper::toDomain)
                .toList();
    }

    @Override
    public void marcarConsumosComoPagos(Long clienteId) {
        List<ConsumoEntity> entidades = jpaRepository.findByClienteIdAndPagoFalse(clienteId);
        entidades.forEach(e -> e.setPago(true));
        jpaRepository.saveAll(entidades);
    }
}
