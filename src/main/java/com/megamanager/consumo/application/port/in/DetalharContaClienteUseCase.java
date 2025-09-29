package com.megamanager.consumo.application.port.in;

import com.megamanager.consumo.application.dto.ExtratoContaCliente;

public interface DetalharContaClienteUseCase {
    ExtratoContaCliente detalharConta(Long clienteId);
}
