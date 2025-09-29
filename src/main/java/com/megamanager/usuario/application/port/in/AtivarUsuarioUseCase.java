package com.megamanager.usuario.application.port.in;

import com.megamanager.usuario.domain.Usuario;

public interface AtivarUsuarioUseCase {

    public Usuario executar(Long id);
}
