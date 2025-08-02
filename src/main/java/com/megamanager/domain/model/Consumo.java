package com.megamanager.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Consumo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4984260509711337992L;
	
	private Long id;
	private Cliente cliente;
	private Produto produto;
	private int quantidade;
	private LocalDateTime dataHora;
	private BigDecimal precoUnitario;
	private BigDecimal precoTotal;

}
