package com.megamanager.fechamento.application.port.in;

import com.megamanager.fechamento.domain.FechamentoConta;

public interface FecharContaClienteUseCase {
    FechamentoConta fechar(Long clienteId);
}
