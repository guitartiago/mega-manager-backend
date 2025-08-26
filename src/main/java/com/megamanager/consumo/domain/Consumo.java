package com.megamanager.consumo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Consumo {

    private Long id;
    private Long clienteId;
    private Long produtoId;
    private Integer quantidade;
    private BigDecimal valorUnitario;   // preço de venda (não-sócio) ou custo do lote (sócio)
    private BigDecimal valorTotal;      // quantidade * valorUnitario (2 casas decimais)
    private LocalDateTime dataHora;
    private boolean pago;
    private Long entradaEstoqueId;      // opcional: lote de estoque usado (FIFO). Pode ser null.

    private Consumo(Long id,
                    Long clienteId,
                    Long produtoId,
                    Integer quantidade,
                    BigDecimal valorUnitario,
                    BigDecimal valorTotal,
                    LocalDateTime dataHora,
                    boolean pago,
                    Long entradaEstoqueId) {

        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId não pode ser null");
        }
        if (produtoId == null) {
            throw new IllegalArgumentException("produtoId não pode ser null");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("quantidade deve ser maior que zero");
        }
        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valorUnitario deve ser maior que zero");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("dataHora não pode ser null");
        }

        // Calcula valorTotal com segurança e padroniza escala
        BigDecimal esperado = valorUnitario
                .multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);

        if (valorTotal == null) {
            valorTotal = esperado;
        } else {
            valorTotal = valorTotal.setScale(2, RoundingMode.HALF_UP);
            if (valorTotal.compareTo(esperado) != 0) {
                throw new IllegalArgumentException("valorTotal inconsistente com quantidade * valorUnitario");
            }
        }

        this.id = id;
        this.clienteId = clienteId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario.setScale(2, RoundingMode.HALF_UP);
        this.valorTotal = valorTotal;
        this.dataHora = dataHora;
        this.pago = pago;
        this.entradaEstoqueId = entradaEstoqueId;
    }

    /** Fábrica para criar um consumo novo (id null, pago=false). */
    public static Consumo criar(Long clienteId,
                                Long produtoId,
                                Integer quantidade,
                                BigDecimal valorUnitario,
                                LocalDateTime dataHora,
                                Long entradaEstoqueId) {
        return new Consumo(
                null,
                clienteId,
                produtoId,
                quantidade,
                valorUnitario,
                null,                // valorTotal calculado internamente
                dataHora,
                false,               // novo consumo nasce não pago
                entradaEstoqueId     // pode ser null se não veio de lote
        );
    }

    /** Fábrica para reconstruir um consumo já existente (ex: ao carregar do repositório). */
    public static Consumo reconstruir(Long id,
                                      Long clienteId,
                                      Long produtoId,
                                      Integer quantidade,
                                      BigDecimal valorUnitario,
                                      BigDecimal valorTotal,
                                      LocalDateTime dataHora,
                                      boolean pago,
                                      Long entradaEstoqueId) {
        return new Consumo(
                id,
                clienteId,
                produtoId,
                quantidade,
                valorUnitario,
                valorTotal,
                dataHora,
                pago,
                entradaEstoqueId
        );
    }

    /** Marca como pago (mutável por simplicidade; se preferir imutável, retorne uma nova instância). */
    public void marcarComoPago() {
        this.pago = true;
    }
}
