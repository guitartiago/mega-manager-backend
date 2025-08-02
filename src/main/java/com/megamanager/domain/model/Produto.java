package com.megamanager.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class Produto implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -1925179718937298911L;
	
	private Long id;
    private String nome;
    private BigDecimal preco;
    private BigDecimal precoCompra;
    private boolean possuiDescontoParaSocio;

}