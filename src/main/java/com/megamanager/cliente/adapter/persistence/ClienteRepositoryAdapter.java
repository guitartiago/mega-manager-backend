package com.megamanager.cliente.adapter.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        ClienteEntity entity = ClienteMapper.toEntity(cliente);
        ClienteEntity saved = jpaRepository.save(entity);
        return ClienteMapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ClienteMapper::toDomain);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpaRepository.findAll()
                .stream()
                .map(ClienteMapper::toDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }
}
