package com.megamanager.consumo.application.port.in;

import com.megamanager.consumo.domain.Consumo;

import java.util.List;

public interface ListarConsumosPorClienteUseCase {
    List<Consumo> listarPorCliente(Long clienteId);
}
