package com.megamanager.usuario.adapter.web.dto;

import com.megamanager.usuario.domain.PerfilUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UsuarioRequestDTO(
        @NotBlank String username,
        @NotBlank String senha,
        @NotEmpty Set<PerfilUsuario> perfis
) {}
