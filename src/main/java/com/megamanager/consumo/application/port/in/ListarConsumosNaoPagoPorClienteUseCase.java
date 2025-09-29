package com.megamanager.consumo.application.port.in;

import com.megamanager.consumo.domain.Consumo;

import java.util.List;

public interface ListarConsumosNaoPagoPorClienteUseCase {
    List<Consumo> listarConsumosNaoPagoPorCliente(Long clienteId);
}
