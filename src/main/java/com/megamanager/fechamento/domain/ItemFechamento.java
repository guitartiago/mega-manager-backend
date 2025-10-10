package com.megamanager.fechamento.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemFechamento {
    private final Long produtoId;
    private final String nomeProduto;
    private final int quantidade;
    private final BigDecimal valorUnitario;
    private final BigDecimal valorTotal;
}
