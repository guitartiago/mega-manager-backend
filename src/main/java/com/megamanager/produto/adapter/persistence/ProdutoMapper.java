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
        Produto produto = new Produto();
        produto.setId(entity.getId());
        produto.setNome(entity.getNome());
        produto.setPrecoVenda(entity.getPrecoVenda());
        produto.setAtivo(entity.isAtivo());
        return produto;
    }
}
