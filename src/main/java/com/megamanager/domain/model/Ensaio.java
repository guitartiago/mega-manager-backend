package com.megamanager.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Ensaio implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 330598492290372720L;
	
	 private Long id;
	 private Banda banda;
	 private LocalDateTime dataHoraInicio;
	 private LocalDateTime dataHoraFim;
	 private BigDecimal valorPorHora;
	
}
