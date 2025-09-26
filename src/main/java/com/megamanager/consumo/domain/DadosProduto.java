package com.megamanager.consumo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Getter;

@Getter
public class DadosProduto {
    private final Long produtoId;
    private final Integer quantidade;
    private final BigDecimal valorUnitario;
    private final BigDecimal valorTotal;

    public DadosProduto(Long produtoId, Integer quantidade, BigDecimal valorUnitario, BigDecimal valorTotal) {
        if (produtoId == null) {
            throw new IllegalArgumentException("produtoId não pode ser null");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("quantidade deve ser maior que zero");
        }
        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
        	valorUnitario = new BigDecimal("0.0");
        }
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario.setScale(2, RoundingMode.HALF_UP);
        this.valorTotal = valorTotal != null
                ? valorTotal.setScale(2, RoundingMode.HALF_UP)
                : valorUnitario.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }
    
    public DadosProduto(Long produtoId, Integer quantidade, BigDecimal valorUnitario) {
    	this(produtoId, quantidade, valorUnitario, null);
    }
}
