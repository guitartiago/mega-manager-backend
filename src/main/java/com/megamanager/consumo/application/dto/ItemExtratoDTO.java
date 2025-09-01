package com.megamanager.consumo.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ItemExtratoDTO {
    private String nomeProduto;
    private int quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
}
