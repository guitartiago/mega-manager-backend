package com.megamanager.lancamento.adapter.persistence;

import com.megamanager.lancamento.domain.CategoriaLancamento;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;

public class LancamentoMapper {

    private LancamentoMapper() {}

    public static LancamentoEntity toEntity(Lancamento dominio) {
        if (dominio == null) {
            return null;
        }
        LancamentoEntity entity = new LancamentoEntity();
        entity.setId(dominio.getId());
        entity.setClienteId(dominio.getClienteId());
        entity.setDataHora(dominio.getDataHora());
        entity.setNatureza(dominio.getNatureza().name());
        entity.setCategoria(dominio.getCategoria().name());
        entity.setValor(dominio.getValor());
        entity.setMotivo(dominio.getMotivo());
        entity.setResponsavelUsername(dominio.getResponsavelUsername());
        entity.setLancamentoOrigemId(dominio.getLancamentoOrigemId());
        entity.setFechamentoId(dominio.getFechamentoId());
        entity.setDataProcessamento(dominio.getDataProcessamento());
        return entity;
    }

    public static Lancamento toDomain(LancamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Lancamento.reconstruir(
                entity.getId(),
                entity.getClienteId(),
                entity.getDataHora(),
                NaturezaLancamento.valueOf(entity.getNatureza()),
                CategoriaLancamento.valueOf(entity.getCategoria()),
                entity.getValor(),
                entity.getMotivo(),
                entity.getResponsavelUsername(),
                entity.getLancamentoOrigemId(),
                entity.getFechamentoId(),
                entity.getDataProcessamento()
        );
    }
}
