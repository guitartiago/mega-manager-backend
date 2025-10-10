package com.megamanager.fechamento.adapter.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FechamentoResumoDTO {
    private Long id;
    private String clienteNome;
    private String usuario;
    private LocalDateTime dataHora;
    private BigDecimal total;
}
