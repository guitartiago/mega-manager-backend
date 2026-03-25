package com.megamanager.lancamento.adapter.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lancamentos")
@Data
public class LancamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String natureza;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(columnDefinition = "text")
    private String motivo;

    @Column(name = "responsavel_username", nullable = false)
    private String responsavelUsername;

    @Column(name = "lancamento_origem_id")
    private Long lancamentoOrigemId;
}
