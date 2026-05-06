package com.megamanager.usuario.application.usecase;

import com.megamanager.usuario.application.port.in.AtualizarUsuarioUseCase;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@RequiredArgsConstructor
public class AtualizarUsuarioService implements AtualizarUsuarioUseCase {

    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario executar(Usuario usuario) {
        usuario.alterarSenha(passwordEncoder.encode(usuario.getSenhaHash()));
        return repository.salvar(usuario);
    }
}
