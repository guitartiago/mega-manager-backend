package com.megamanager.cliente.application.port.in;

import com.megamanager.cliente.domain.Cliente;
import java.util.Optional;

public interface AtualizarClienteUseCase {
    Optional<Cliente> atualizar(Long id, Cliente clienteAtualizado);
}
