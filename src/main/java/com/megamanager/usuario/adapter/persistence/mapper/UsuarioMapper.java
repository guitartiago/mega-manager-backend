package com.megamanager.usuario.adapter.persistence.mapper;

import com.megamanager.usuario.adapter.persistence.entity.UsuarioEntity;
import com.megamanager.usuario.domain.Usuario;

public class UsuarioMapper {

    public static Usuario toDomain(UsuarioEntity entity) {
        return Usuario.reconstruir(
                entity.getId(),
                entity.getUsername(),
                entity.getSenhaHash(),
                entity.isAtivo(),
                entity.getPerfis()
        );
    }

    public static UsuarioEntity toEntity(Usuario usuario) {
        return UsuarioEntity.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .senhaHash(usuario.getSenhaHash())
                .ativo(usuario.isAtivo())
                .perfis(usuario.getPerfis())
                .build();
    }
}
