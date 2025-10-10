package com.megamanager.fechamento.adapter.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FechamentoResponseDTO {
    private Long id;
    private Long clienteId;
    private String clienteNome;
    private String usuario;
    private LocalDateTime dataHora;
    private BigDecimal total;
    private List<ItemFechamentoDTO> itens;
}
