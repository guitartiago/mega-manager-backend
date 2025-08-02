package com.megamanager.adapter.persistence.mapper;

import com.megamanager.domain.model.Cliente;
import com.megamanager.infrastructure.entity.ClienteEntity;

public class ClienteMapper {

    public static ClienteEntity toEntity(Cliente cliente) {
        if (cliente == null) return null;

        ClienteEntity entity = new ClienteEntity();
        entity.setId(cliente.getId());
        entity.setNome(cliente.getNome());
        entity.setEmail(cliente.getEmail());
        entity.setPerfil(cliente.getPerfil());
        return entity;
    }

    public static Cliente toDomain(ClienteEntity entity) {
        if (entity == null) return null;

        Cliente cliente = new Cliente();
        cliente.setId(entity.getId());
        cliente.setNome(entity.getNome());
        cliente.setEmail(entity.getEmail());
        cliente.setPerfil(entity.getPerfil());
        return cliente;
    }
}
