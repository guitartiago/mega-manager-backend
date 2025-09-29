package com.megamanager.usuario.application.usecase;

import java.util.List;

import com.megamanager.usuario.application.port.in.ListarUsuariosUseCase;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarUsuariosService implements ListarUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;
 
    @Override
    public List<Usuario> executar() {
        return usuarioRepository.buscarTodos();
    }
}
