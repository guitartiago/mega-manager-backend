package com.megamanager.lancamento.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Lancamento {

    private Long id;
    private Long clienteId;
    private LocalDateTime dataHora;
    private NaturezaLancamento natureza;
    private CategoriaLancamento categoria;
    private BigDecimal valor;
    private String motivo;
    private String responsavelUsername;
    private Long lancamentoOrigemId; // null para lançamentos normais; preenchido para ESTORNO

    private Long fechamentoId;
    private LocalDateTime dataProcessamento;

    private Lancamento(Long id,
                       Long clienteId,
                       LocalDateTime dataHora,
                       NaturezaLancamento natureza,
                       CategoriaLancamento categoria,
                       BigDecimal valor,
                       String motivo,
                       String responsavelUsername,
                       Long lancamentoOrigemId,
                       Long fechamentoId,              // NOVO
                       LocalDateTime dataProcessamento) {

        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId não pode ser null");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("dataHora não pode ser null");
        }
        if (natureza == null) {
            throw new IllegalArgumentException("natureza não pode ser null");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("categoria não pode ser null");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor deve ser maior que zero");
        }
        if (responsavelUsername == null || responsavelUsername.isBlank()) {
            throw new IllegalArgumentException("responsavelUsername não pode ser vazio");
        }

        // motivo obrigatório para categorias sensíveis
        if ((categoria == CategoriaLancamento.DESCONTO || categoria == CategoriaLancamento.CORRECAO)
                && (motivo == null || motivo.isBlank())) {
            throw new IllegalArgumentException("motivo é obrigatório para DESCONTO/CORRECAO");
        }

        // estorno sempre precisa apontar para a origem e ter motivo
        if (categoria == CategoriaLancamento.ESTORNO) {
            if (lancamentoOrigemId == null) {
                throw new IllegalArgumentException("lancamentoOrigemId é obrigatório para ESTORNO");
            }
            if (motivo == null || motivo.isBlank()) {
                throw new IllegalArgumentException("motivo é obrigatório para ESTORNO");
            }
        }

        this.id = id;
        this.clienteId = clienteId;
        this.dataHora = dataHora;
        this.natureza = natureza;
        this.categoria = categoria;
        this.valor = valor;
        this.motivo = motivo;
        this.responsavelUsername = responsavelUsername;
        this.lancamentoOrigemId = lancamentoOrigemId;
        this.fechamentoId = fechamentoId;              // NOVO
        this.dataProcessamento = dataProcessamento;    // NOVO
    }

    public static Lancamento criar(Long clienteId,
                                   LocalDateTime dataHora,
                                   NaturezaLancamento natureza,
                                   CategoriaLancamento categoria,
                                   BigDecimal valor,
                                   String motivo,
                                   String responsavelUsername) {
        return new Lancamento(
                null,
                clienteId,
                dataHora,
                natureza,
                categoria,
                valor,
                motivo,
                responsavelUsername,
                null,
                null,
                null
        );
    }

    public static Lancamento reconstruir(Long id,
                                         Long clienteId,
                                         LocalDateTime dataHora,
                                         NaturezaLancamento natureza,
                                         CategoriaLancamento categoria,
                                         BigDecimal valor,
                                         String motivo,
                                         String responsavelUsername,
                                         Long lancamentoOrigemId,
                                         Long fechamentoId,
                                         LocalDateTime dataProcessamento) {
        return new Lancamento(
                id,
                clienteId,
                dataHora,
                natureza,
                categoria,
                valor,
                motivo,
                responsavelUsername,
                lancamentoOrigemId,
                fechamentoId,
                dataProcessamento
        );
    }

    public Lancamento gerarEstorno(String motivoEstorno, String responsavelUsernameEstorno) {
        NaturezaLancamento naturezaInvertida = (this.natureza == NaturezaLancamento.DEBITO)
                ? NaturezaLancamento.CREDITO
                : NaturezaLancamento.DEBITO;

        return new Lancamento(
                null,
                this.clienteId,
                LocalDateTime.now(),
                naturezaInvertida,
                CategoriaLancamento.ESTORNO,
                this.valor,
                motivoEstorno,
                responsavelUsernameEstorno,
                this.id,
                null,
                null
        );
    }
}
