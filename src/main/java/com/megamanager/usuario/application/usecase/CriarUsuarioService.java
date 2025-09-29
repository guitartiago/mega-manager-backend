package com.megamanager.usuario.application.usecase;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.megamanager.usuario.application.port.in.CriarUsuarioUseCase;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarUsuarioService implements CriarUsuarioUseCase{

    private final UsuarioRepository repository;    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario executar(Usuario usuario) {
    	
        return repository.salvar(Usuario.builder()
        		.username(usuario.getUsername())
        		.senhaHash(passwordEncoder.encode(usuario.getSenhaHash()))
        		.ativo(usuario.isAtivo())
        		.perfis(usuario.getPerfis())
        		.build());
    }
}
