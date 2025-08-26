package com.megamanager.produto.adapter.persistence;

import com.megamanager.produto.domain.Produto;

public class ProdutoMapper {

    public static ProdutoEntity toEntity(Produto produto) {
        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(produto.getId());
        entity.setNome(produto.getNome());
        entity.setPrecoVenda(produto.getPrecoVenda());
        entity.setAtivo(produto.isAtivo());
        return entity;
    }

    public static Produto toDomain(ProdutoEntity entity) {       
        return Produto.reconstruir(entity.getId(), entity.getNome(), entity.getPrecoVenda(), entity.isAtivo());
    }
}
