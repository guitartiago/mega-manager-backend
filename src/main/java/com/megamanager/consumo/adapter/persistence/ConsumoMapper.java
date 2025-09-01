package com.megamanager.consumo.adapter.persistence;

import com.megamanager.consumo.domain.Consumo;
import com.megamanager.consumo.domain.DadosProduto;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ConsumoMapper {

    public static ConsumoEntity toEntity(Consumo consumo) {
        ConsumoEntity entity = new ConsumoEntity();
        entity.setId(consumo.getId());
        entity.setClienteId(consumo.getClienteId());
        entity.setProdutoId(consumo.getDadosProduto().getProdutoId());
        entity.setQuantidade(consumo.getDadosProduto().getQuantidade());
        entity.setValorUnitario(consumo.getDadosProduto().getValorUnitario());
        entity.setValorTotal(consumo.getDadosProduto().getValorTotal());
        entity.setDataHora(consumo.getDataHora());
        entity.setPago(consumo.isPago());
        entity.setEntradaEstoqueId(consumo.getEntradaEstoqueId());
        return entity;
    }

    public static Consumo toDomain(ConsumoEntity entity) {
        return Consumo.reconstruir(
                entity.getId(),
                entity.getClienteId(),
                new DadosProduto(
                		entity.getProdutoId(),
                		entity.getQuantidade(),
                		entity.getValorUnitario(),
                		entity.getValorTotal()
                ),
                entity.getDataHora(),
                entity.isPago(),
                entity.getEntradaEstoqueId()
        );
    }
}
