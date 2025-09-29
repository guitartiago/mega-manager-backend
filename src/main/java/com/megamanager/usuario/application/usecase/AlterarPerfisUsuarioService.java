package com.megamanager.usuario.application.usecase;

import java.util.Set;

import com.megamanager.usuario.application.port.in.AlterarPerfisUsuarioUseCase;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.domain.PerfilUsuario;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AlterarPerfisUsuarioService implements AlterarPerfisUsuarioUseCase {

    private final UsuarioRepository repository;
    
    @Override
    public void executar(Long id, Set<PerfilUsuario> novosPerfis) {
        var usuario = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        usuario.alterarPerfis(novosPerfis);
        repository.salvar(usuario);
    }
}
