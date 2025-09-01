package com.megamanager.consumo.application.port.in;

import com.megamanager.consumo.application.dto.ExtratoContaCliente;

public interface FecharContaClienteUseCase {
    ExtratoContaCliente fecharConta(Long clienteId);
}
