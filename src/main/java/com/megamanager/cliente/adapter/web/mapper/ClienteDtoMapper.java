package com.megamanager.cliente.adapter.web.mapper;

import com.megamanager.cliente.adapter.web.dto.ClienteRequestDTO;
import com.megamanager.cliente.adapter.web.dto.ClienteResponseDTO;
import com.megamanager.cliente.domain.Cliente;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ClienteDtoMapper {

    public static Cliente toDomain(ClienteRequestDTO dto) {
        return  Cliente.criar(dto.getNome()
        		, dto.getEmail()
        		, dto.getCelular(), dto.getPerfil());
    }

    public static ClienteResponseDTO toResponse(Cliente cliente) {
        return new ClienteResponseDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getCelular(),
            cliente.getPerfil()
        );
    }
}
