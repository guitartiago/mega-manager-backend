package com.megamanager.notificacao.application.port.in;

import java.math.BigDecimal;

public interface EnviarContaPixEmailUseCase {
    record Command(
            String emailDestino,
            String nomeCliente,
            BigDecimal valor,
            String descricao,
            boolean anexarArquivos
    ) {}

    void execute(Command command);
}
