package com.megamanager.estoque.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

@Getter
public class EntradaEstoque {
	
    private static final Logger log = LoggerFactory.getLogger(EntradaEstoque.class);

    private Long id;
    private Long produtoId;
    private int quantidade;
    private BigDecimal precoCustoUnitario;
    private LocalDateTime dataCompra;
    private int saldo; 
    
    private EntradaEstoque(Long id
    		, Long produtoId
    		, int quantidade
    		, BigDecimal precoCustoUnitario,
			LocalDateTime dataCompra
			, int saldo) {
		
    	if(produtoId == null) {
    		throw new IllegalArgumentException("Id do Produto não pode ser null");    		
    	} 
    	
    	if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que 0 (Zero)");
        }

    	if (precoCustoUnitario == null || precoCustoUnitario.doubleValue() <= 0) {
    		throw new IllegalArgumentException("preço do custo unitário não deve ser nullo ou menor/igual a 0 (Zero)");
    	}
    	
    	if (dataCompra == null) {
    		throw new IllegalArgumentException("Data da Compra não deve ser Null");
    	}
    	    	
    	this.id = id;
		this.produtoId = produtoId;
		this.quantidade = quantidade;
		this.precoCustoUnitario = precoCustoUnitario;
		this.dataCompra = dataCompra;
		this.saldo = saldo;
	}
    
    public static EntradaEstoque reconstruir(Long id
    		, Long produtoId
    		, int quantidade
    		, BigDecimal precoCustoUnitario
    		, LocalDateTime dataCompra
    		, int saldo) {
    	return new EntradaEstoque(id, produtoId, quantidade, precoCustoUnitario, dataCompra, saldo);
    }
    
    public static EntradaEstoque criar(Long produtoId
    		, int quantidade
    		, BigDecimal precoCustoUnitario
    		, LocalDateTime dataCompra) {
    	return new EntradaEstoque(null, produtoId, quantidade, precoCustoUnitario, dataCompra, quantidade);
    }
    
    public boolean possuiSaldoDisponivel() {
        return saldo > 0;
    }

    public void abaterSaldo(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a abater deve ser maior que zero");
        }

        if (quantidade > this.saldo) {
           log.warn("⚠️ Atenção: tentando abater mais do que o saldo disponível. Estoque ficará negativo.");
        }

        this.saldo -= quantidade;
    }

	
}
