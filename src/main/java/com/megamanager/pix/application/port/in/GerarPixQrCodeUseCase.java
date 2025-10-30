package com.megamanager.pix.application.port.in;

import java.math.BigDecimal;

public interface GerarPixQrCodeUseCase {
    record Command(BigDecimal valor, String descricao) {}
    record Result(String txid, String payload, byte[] png) {}

    Result execute(Command command);
}
