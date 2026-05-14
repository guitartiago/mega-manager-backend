package com.megamanager.consumo.adapter.web.dto;

import com.megamanager.lancamento.domain.NaturezaLancamento;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LancamentoContaDTO {

    private Long id;
    private Long clienteId;
    private LocalDateTime dataHora;

    private NaturezaLancamento natureza;

    private String categoria;
    private BigDecimal valor;
    private String motivo;
    private String responsavelUsername;
    private Boolean aberto;

    // getters e setters
}