package com.megamanager.cliente.application.usecase;

import java.util.List;
import java.util.Optional;

import com.megamanager.cliente.application.port.in.AtualizarClienteUseCase;
import com.megamanager.cliente.application.port.in.BuscarClienteUseCase;
import com.megamanager.cliente.application.port.in.CadastrarClienteUseCase;
import com.megamanager.cliente.application.port.in.ExcluirClienteUseCase;
import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GerenciarClienteService implements
        CadastrarClienteUseCase,
        BuscarClienteUseCase,
        AtualizarClienteUseCase,
        ExcluirClienteUseCase {

    private final ClienteRepository clienteRepository;

    @Override
    public Cliente cadastrar(Cliente cliente) {
        return clienteRepository.salvar(cliente);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.buscarPorId(id);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    @Override
    public Optional<Cliente> atualizar(Long id, Cliente clienteAtualizado) {
        return clienteRepository.buscarPorId(id)
                .map(clienteExistente -> {
                    clienteAtualizado.setId(id);
                    return clienteRepository.salvar(clienteAtualizado);
                });
    }

    @Override
    public void excluir(Long id) {
        clienteRepository.excluir(id);
    }
}
