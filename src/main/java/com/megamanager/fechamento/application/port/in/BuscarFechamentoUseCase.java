package com.megamanager.fechamento.application.port.in;

import java.util.Optional;
import com.megamanager.fechamento.domain.FechamentoConta;

public interface BuscarFechamentoUseCase {
    Optional<FechamentoConta> buscarPorId(Long fechamentoId);
}
