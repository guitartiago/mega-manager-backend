package com.megamanager.usuario.adapter.web.dto;

import com.megamanager.usuario.domain.PerfilUsuario;

import java.util.Set;

public record UsuarioResponseDTO(
        Long id,
        String username,
        Set<PerfilUsuario> perfis,
        boolean ativo
) {}
