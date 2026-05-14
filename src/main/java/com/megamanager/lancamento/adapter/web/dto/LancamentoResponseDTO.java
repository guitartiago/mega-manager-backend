package com.megamanager.lancamento.adapter.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LancamentoResponseDTO {

    private Long id;
    private Long clienteId;
    private LocalDateTime dataHora;
    private String natureza;
    private String categoria;
    private BigDecimal valor;
    private String motivo;
    private String responsavelUsername;
    private Long lancamentoOrigemId;
    private Long fechamentoId;
    private LocalDateTime dataProcessamento;
}
