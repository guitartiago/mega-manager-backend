package com.megamanager.produto.domain;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Produto {
	@Setter
    private Long id;
    
	private String nome;         
    private BigDecimal precoVenda;  
    private boolean ativo;  
    
    private Produto(Long id, String nome, BigDecimal precoVenda, boolean ativo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (precoVenda == null || precoVenda.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser maior que zero");
        }

        this.id = id;
        this.nome = nome;
        this.precoVenda = precoVenda;
        this.ativo = ativo;
    }

    public static Produto reconstruir(Long id, String nome, BigDecimal precoVenda, boolean ativo) {
        return new Produto(id, nome, precoVenda, ativo);
    }

    public static Produto criar(String nome, BigDecimal precoVenda) {
        return new Produto(null, nome, precoVenda, true);
    }
    
}
