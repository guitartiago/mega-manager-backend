package com.megamanager.pix.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class PixPagamento {
    private final BigDecimal valor;
    private final String txid;
    private final String descricao;
    private final Instant criadoEm;

    public PixPagamento(BigDecimal valor, String txid, String descricao) {
        this.valor = valor;
        this.txid = txid;
        this.descricao = descricao;
        this.criadoEm = Instant.now();
    }

    public BigDecimal getValor() { return valor; }
    public String getTxid() { return txid; }
    public String getDescricao() { return descricao; }
    public Instant getCriadoEm() { return criadoEm; }
}
