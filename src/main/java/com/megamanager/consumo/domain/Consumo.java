package com.megamanager.consumo.domain;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Consumo {

    private Long id;
    private Long clienteId;
    private DadosProduto dadosProduto;
    private LocalDateTime dataHora;
    private boolean pago;
    private Long entradaEstoqueId;

    private Consumo(Long id,
                    Long clienteId,
                    DadosProduto dadosProduto,
                    LocalDateTime dataHora,
                    boolean pago,
                    Long entradaEstoqueId) {

        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId não pode ser null");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("dataHora não pode ser null");
        }

        this.id = id;
        this.clienteId = clienteId;
        this.dadosProduto = dadosProduto;
        this.dataHora = dataHora;
        this.pago = pago;
        this.entradaEstoqueId = entradaEstoqueId;
    }

    public static Consumo criar(Long clienteId,
                                DadosProduto dadosProduto,
                                LocalDateTime dataHora,
                                Long entradaEstoqueId) {
        return new Consumo(
                null,
                clienteId,
                dadosProduto,
                dataHora,
                false,
                entradaEstoqueId
        );
    }

    public static Consumo reconstruir(Long id,
                                      Long clienteId,
                                      DadosProduto dadosProduto,
                                      LocalDateTime dataHora,
                                      boolean pago,
                                      Long entradaEstoqueId) {
        return new Consumo(
                id,
                clienteId,
                dadosProduto,
                dataHora,
                pago,
                entradaEstoqueId
        );
    }

    public void marcarComoPago() {
        this.pago = true;
    }
}
