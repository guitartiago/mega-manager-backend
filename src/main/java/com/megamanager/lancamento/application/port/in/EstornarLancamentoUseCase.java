package com.megamanager.lancamento.application.port.in;

import com.megamanager.lancamento.domain.Lancamento;

public interface EstornarLancamentoUseCase {

    record EstornarLancamentoCommand(
            Long lancamentoId,
            String motivo,
            String responsavelUsername
    ) {}

    Lancamento executar(EstornarLancamentoCommand command);
}
