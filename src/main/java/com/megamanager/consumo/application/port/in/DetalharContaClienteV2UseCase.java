package com.megamanager.consumo.application.port.in;

import com.megamanager.consumo.adapter.web.dto.DetalheContaDTO;
import com.megamanager.consumo.application.dto.ExtratoContaCliente;

public interface DetalharContaClienteV2UseCase {
    DetalheContaDTO execute(Long clienteId);
}
