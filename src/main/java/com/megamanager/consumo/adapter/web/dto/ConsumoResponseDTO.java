package com.megamanager.consumo.adapter.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumoResponseDTO {

    private Long id;
    private Long clienteId;
    private Long produtoId;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private LocalDateTime dataHora;
    private boolean pago;
    private Long entradaEstoqueId; // pode ser null
}
