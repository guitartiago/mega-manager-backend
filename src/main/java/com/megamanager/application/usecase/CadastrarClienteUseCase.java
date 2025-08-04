package com.megamanager.application.usecase;

import com.megamanager.application.port.out.ClienteRepository;
import com.megamanager.domain.model.Cliente;

public class CadastrarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public CadastrarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente executar(Cliente cliente) {
        return clienteRepository.salvar(cliente);
    }
}
