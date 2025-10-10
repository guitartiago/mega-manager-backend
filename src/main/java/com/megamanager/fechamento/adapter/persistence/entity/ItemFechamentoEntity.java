package com.megamanager.fechamento.adapter.persistence.entity;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name="produto_id", nullable=false)
    private Long produtoId;

    @Column(name="nome_produto", nullable=false)
    private String nomeProduto;

    @Column(nullable=false)
    private Integer quantidade;

    @Column(name="valor_unitario", nullable=false, precision=10, scale=2)
    private java.math.BigDecimal valorUnitario;

    @Column(name="valor_total", nullable=false, precision=10, scale=2)
    private java.math.BigDecimal valorTotal;
}
