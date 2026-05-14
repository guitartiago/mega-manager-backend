package com.megamanager.fechamento.adapter.web.dto;

import java.math.BigDecimal;

import com.megamanager.lancamento.domain.NaturezaLancamento;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemFechamentoDTO {
    private Long produtoId;
    private String nomeProduto;
    private int quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    private String tipoItem;              // "CONSUMO" ou "LANCAMENTO"
    private Long lancamentoId;            // null se for consumo
    private String descricao;             // Descrição do lançamento
    private NaturezaLancamento natureza;  // DEBITO ou CREDITO (null para consumos)
}
