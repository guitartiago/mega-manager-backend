package com.megamanager.usuario.application.port.in;

import com.megamanager.usuario.domain.Usuario;

public interface AtualizarUsuarioUseCase {
    public Usuario executar(Usuario usuario);
}
