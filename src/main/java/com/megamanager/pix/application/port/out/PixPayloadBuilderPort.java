package com.megamanager.pix.application.port.out;

import java.math.BigDecimal;

public interface PixPayloadBuilderPort {
    String build(String chavePix, String nome, String cidade, String gui,
                 BigDecimal valor, String txid, String descricao);
}
