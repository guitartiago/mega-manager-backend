package com.megamanager.fechamento.application.port.in;

import java.time.LocalDateTime;
import java.util.List;
import com.megamanager.fechamento.domain.FechamentoConta;

public interface ListarFechamentosUseCase {
    List<FechamentoConta> listar(Long clienteId, LocalDateTime de, LocalDateTime ate);
}
