package com.megamanager.usuario.application.port.in;

import java.util.Set;

import com.megamanager.usuario.domain.PerfilUsuario;

public interface AlterarPerfisUsuarioUseCase {
    public void executar(Long id, Set<PerfilUsuario> novosPerfis);
}
