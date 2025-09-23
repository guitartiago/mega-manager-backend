package com.megamanager.cliente.adapter.persistence;

import com.megamanager.cliente.domain.Cliente;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ClienteMapper {

    public static ClienteEntity toEntity(Cliente cliente) {
        if (cliente == null) return null;

        ClienteEntity entity = new ClienteEntity();
        entity.setId(cliente.getId());
        entity.setNome(cliente.getNome());
        entity.setEmail(cliente.getEmail());
        entity.setCelular(cliente.getCelular());
        entity.setPerfil(cliente.getPerfil());
        return entity;
    }

    public static Cliente toDomain(ClienteEntity entity) {
        if (entity == null) return null;

        return Cliente.reconstruir(entity.getId()
        		, entity.getNome()
        		, entity.getEmail()
        		, entity.getCelular(), entity.getPerfil());
    }
}
