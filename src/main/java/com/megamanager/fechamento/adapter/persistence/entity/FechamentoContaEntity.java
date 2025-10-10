package com.megamanager.fechamento.adapter.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fechamentos_conta")
@Data
public class FechamentoContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="cliente_id", nullable=false)
    private Long clienteId;

    @Column(name="cliente_nome", nullable=false)
    private String clienteNome;

    @Column(name="usuario_username", nullable=false)
    private String usuarioUsername;

    @Column(name="data_hora", nullable=false)
    private LocalDateTime dataHora;

    @Column(name="total_pago", nullable=false, precision = 10, scale = 2)
    private BigDecimal totalPago;

    @OneToMany(mappedBy = "fechamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemFechamentoEntity> itens;
}
