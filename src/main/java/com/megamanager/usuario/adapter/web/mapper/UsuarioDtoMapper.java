package com.megamanager.usuario.adapter.web.mapper;

import com.megamanager.usuario.adapter.web.dto.UsuarioResponseDTO;
import com.megamanager.usuario.domain.Usuario;

public class UsuarioDtoMapper {

    public static UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPerfis(),
                usuario.isAtivo()
        );
    }
}
