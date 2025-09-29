package com.megamanager.usuario.application.port.out;

import java.util.List;
import java.util.Optional;

import com.megamanager.usuario.domain.Usuario;

public interface UsuarioRepository {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorUsername(String username);
	List<Usuario> buscarTodos();
    
}
