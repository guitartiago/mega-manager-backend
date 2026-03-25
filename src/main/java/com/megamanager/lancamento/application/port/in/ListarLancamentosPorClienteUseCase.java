package com.megamanager.lancamento.application.port.in;

import java.util.List;

import com.megamanager.lancamento.domain.Lancamento;

public interface ListarLancamentosPorClienteUseCase {

    List<Lancamento> listarPorCliente(Long clienteId);
}
