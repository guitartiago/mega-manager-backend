package com.megamanager.estoque.adapter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EntradaEstoqueResponseDTO {

    private Long id;
    private Long produtoId;
    private Integer quantidade;
    private BigDecimal precoCustoUnitario;
    private LocalDateTime dataCompra;
    private Integer saldo;
}
