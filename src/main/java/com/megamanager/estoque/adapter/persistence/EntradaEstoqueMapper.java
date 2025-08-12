package com.megamanager.estoque.adapter.persistence;

import com.megamanager.estoque.domain.EntradaEstoque;

public class EntradaEstoqueMapper {

    public static EntradaEstoqueEntity toEntity(EntradaEstoque entradaEstoque) {
        EntradaEstoqueEntity entity = new EntradaEstoqueEntity();
        entity.setId(entradaEstoque.getId());
        entity.setProdutoId(entradaEstoque.getProdutoId());
        entity.setQuantidade(entradaEstoque.getQuantidade());
        entity.setPrecoCustoUnitario(entradaEstoque.getPrecoCustoUnitario());
        entity.setDataCompra(entradaEstoque.getDataCompra());
        entity.setSaldo(entradaEstoque.getSaldo());
        return entity;
    }

    public static EntradaEstoque toDomain(EntradaEstoqueEntity entity) {
        EntradaEstoque entradaEstoque = EntradaEstoque.criar(entity.getId()
        		, entity.getProdutoId()
        		, entity.getQuantidade()
        		, entity.getPrecoCustoUnitario()
        		, entity.getDataCompra()
        		, entity.getSaldo());
       
        return entradaEstoque;
    }
}
