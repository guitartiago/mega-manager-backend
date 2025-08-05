package com.megamanager.cliente.application.port.in;

import com.megamanager.cliente.domain.Cliente;

public interface CadastrarClienteUseCase {
    Cliente cadastrar(Cliente cliente);
}
