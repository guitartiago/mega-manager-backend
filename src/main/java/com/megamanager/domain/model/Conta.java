package com.megamanager.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.megamanager.cliente.domain.Cliente;

import lombok.Data;

@Data
public class Conta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4110773742583859356L;
	
	private Long id;
	private Ensaio ensaio;
	private List<Consumo> itensDeConsumo;
	private Map<Cliente, BigDecimal> valoresPorCliente;
	private BigDecimal total;
	private LocalDateTime dataFechamento;

}
