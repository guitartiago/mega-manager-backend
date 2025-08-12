package com.megamanager.estoque.adapter.persistence;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "entradas_estoque")
@Data
public class EntradaEstoqueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_custo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCustoUnitario;

    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;

    @Column(nullable = false)
    private Integer saldo;
}
