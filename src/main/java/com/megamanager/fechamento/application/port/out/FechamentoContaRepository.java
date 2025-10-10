package com.megamanager.fechamento.application.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.megamanager.fechamento.domain.FechamentoConta;

public interface FechamentoContaRepository {
    FechamentoConta salvar(FechamentoConta fechamento);
    Optional<FechamentoConta> buscarPorId(Long id);
    List<FechamentoConta> listar(Long clienteId, LocalDateTime de, LocalDateTime ate);
}
