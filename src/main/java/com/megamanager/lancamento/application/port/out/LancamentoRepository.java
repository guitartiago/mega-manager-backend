package com.megamanager.lancamento.application.port.out;

import java.util.List;
import java.util.Optional;

import com.megamanager.lancamento.domain.Lancamento;

public interface LancamentoRepository {

    Lancamento salvar(Lancamento lancamento);

    List<Lancamento> buscarPorCliente(Long clienteId);

    Optional<Lancamento> buscarPorId(Long id);

    boolean existeEstornoParaOrigem(Long lancamentoOrigemId);

    List<Lancamento> buscarNaoProcessadosPorCliente(Long clienteId);

    void marcarLancamentosComoProcessados(List<Long> lancamentoIds, Long fechamentoId);
}
