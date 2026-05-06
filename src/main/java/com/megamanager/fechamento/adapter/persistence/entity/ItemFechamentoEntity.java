package com.megamanager.fechamento.adapter.persistence.entity;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;

import com.megamanager.lancamento.domain.NaturezaLancamento;

@Entity
@Table(name = "itens_fechamento")
@Data
public class ItemFechamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="fechamento_id")
    private FechamentoContaEntity fechamento;

    @Column(name="produto_id", nullable=true)
    private Long produtoId;

    @Column(name="nome_produto", nullable=true)
    private String nomeProduto;

    @Column(nullable=true)
    private Integer quantidade;

    @Column(name="valor_unitario", nullable=true, precision=10, scale=2)
    private java.math.BigDecimal valorUnitario;

    @Column(name="valor_total", nullable=false, precision=10, scale=2)
    private java.math.BigDecimal valorTotal;

    @Column(name="tipo_item", nullable=false)
    private String tipoItem;  // "CONSUMO" ou "LANCAMENTO"

    @Column(name="lancamento_id", nullable=true)
    private Long lancamentoId;

    @Column(nullable=true)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable=true)
    private NaturezaLancamento natureza;
}