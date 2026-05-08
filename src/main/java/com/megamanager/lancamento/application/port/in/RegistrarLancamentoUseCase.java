package com.megamanager.lancamento.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.megamanager.lancamento.domain.CategoriaLancamento;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;

public interface RegistrarLancamentoUseCase {

    record RegistrarLancamentoCommand(
            Long clienteId,
            LocalDateTime dataHora,
            NaturezaLancamento natureza,
            CategoriaLancamento categoria,
            BigDecimal valor,
            String motivo,
            String responsavelUsername,
            boolean responsavelEhAdmin
    ) {}

    Lancamento executar(RegistrarLancamentoCommand command);
}
