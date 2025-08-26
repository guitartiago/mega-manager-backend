package com.megamanager.consumo.adapter.persistence;

import com.megamanager.consumo.domain.Consumo;

public class ConsumoMapper {

    public static ConsumoEntity toEntity(Consumo consumo) {
        ConsumoEntity entity = new ConsumoEntity();
        entity.setId(consumo.getId());
        entity.setClienteId(consumo.getClienteId());
        entity.setProdutoId(consumo.getProdutoId());
        entity.setQuantidade(consumo.getQuantidade());
        entity.setValorUnitario(consumo.getValorUnitario());
        entity.setValorTotal(consumo.getValorTotal());
        entity.setDataHora(consumo.getDataHora());
        entity.setPago(consumo.isPago());
        entity.setEntradaEstoqueId(consumo.getEntradaEstoqueId());
        return entity;
    }

    public static Consumo toDomain(ConsumoEntity entity) {
        return Consumo.reconstruir(
                entity.getId(),
                entity.getClienteId(),
                entity.getProdutoId(),
                entity.getQuantidade(),
                entity.getValorUnitario(),
                entity.getValorTotal(),
                entity.getDataHora(),
                entity.isPago(),
                entity.getEntradaEstoqueId()
        );
    }
}
