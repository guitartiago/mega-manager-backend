package com.megamanager.usuario.application.usecase;

import com.megamanager.usuario.application.port.in.AtualizarUsuarioUseCase;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AtualizarUsuarioService implements AtualizarUsuarioUseCase {

    private final UsuarioRepository repository;
     
    @Override
    public Usuario executar(Usuario usuario) {
        return repository.salvar(usuario);
    }
}
