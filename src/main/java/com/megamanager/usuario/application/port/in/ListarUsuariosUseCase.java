package com.megamanager.usuario.application.port.in;

import java.util.List;

import com.megamanager.usuario.domain.Usuario;

public interface ListarUsuariosUseCase {
    public List<Usuario> executar();
}
