package com.megamanager.fechamento.domain;

import java.math.BigDecimal;

import com.megamanager.lancamento.domain.NaturezaLancamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ItemFechamento {
    private final Long produtoId;
    private final String nomeProduto;
    private final int quantidade;
    private final BigDecimal valorUnitario;
    private final BigDecimal valorTotal;

    private String tipoItem;              // "CONSUMO" ou "LANCAMENTO"
    private Long lancamentoId;            // null se for consumo
    private String descricao;             // Descrição do lançamento
    private NaturezaLancamento natureza;  // DEBITO ou CREDITO (null para consumos)
}
