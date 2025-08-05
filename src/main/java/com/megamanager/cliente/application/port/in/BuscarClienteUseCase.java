package com.megamanager.cliente.application.port.in;

import com.megamanager.cliente.domain.Cliente;
import java.util.Optional;
import java.util.List;

public interface BuscarClienteUseCase {
    Optional<Cliente> buscarPorId(Long id);
    List<Cliente> listarTodos();
}
