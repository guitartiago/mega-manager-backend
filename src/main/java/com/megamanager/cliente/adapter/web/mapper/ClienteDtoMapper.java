package com.megamanager.cliente.adapter.web.mapper;

import com.megamanager.cliente.adapter.web.dto.ClienteRequestDTO;
import com.megamanager.cliente.adapter.web.dto.ClienteResponseDTO;
import com.megamanager.cliente.domain.Cliente;

public class ClienteDtoMapper {

    public static Cliente toDomain(ClienteRequestDTO dto) {
        Cliente cliente = Cliente.criar(dto.getNome()
        		, dto.getEmail()
        		, dto.getPerfil());
        return cliente;
    }

    public static ClienteResponseDTO toResponse(Cliente cliente) {
        return new ClienteResponseDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getPerfil()
        );
    }
}
