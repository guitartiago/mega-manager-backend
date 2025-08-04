package com.megamanager.application.usecase;

import com.megamanager.application.port.out.ClienteRepository;
import com.megamanager.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public class GerenciarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public GerenciarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(Cliente cliente) {
        return clienteRepository.salvar(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.buscarPorId(id);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public Optional<Cliente> atualizar(Long id, Cliente clienteAtualizado) {
        return clienteRepository.buscarPorId(id).map(clienteExistente -> {
            clienteAtualizado.setId(id);
            return clienteRepository.salvar(clienteAtualizado);
        });
    }

    public void excluir(Long id) {
        clienteRepository.excluir(id);
    }
}
