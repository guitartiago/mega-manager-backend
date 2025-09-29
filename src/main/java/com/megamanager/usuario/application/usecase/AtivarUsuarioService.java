package com.megamanager.usuario.application.usecase;

import com.megamanager.usuario.application.port.in.AtivarUsuarioUseCase;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AtivarUsuarioService implements AtivarUsuarioUseCase {

    private final UsuarioRepository repository;
    
    @Override
    public Usuario executar(Long id) {
        var usuario = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        usuario.ativar();
        return repository.salvar(usuario);
    }
}
