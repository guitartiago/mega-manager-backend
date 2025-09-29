package com.megamanager.auth.application.usecase;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.megamanager.auth.application.port.in.AutenticarUseCase;
import com.megamanager.auth.application.port.out.TokenProvider;
import com.megamanager.usuario.application.port.out.UsuarioRepository;
import com.megamanager.usuario.domain.Usuario;

public class AutenticarService implements AutenticarUseCase {
  private final UsuarioRepository repo;
  private final TokenProvider tokenProvider;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AutenticarService(UsuarioRepository repo, TokenProvider tokenProvider) {
    this.repo = repo;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public TokenDTO autenticar(String username, String senha) {
    Usuario u = repo.buscarPorUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Usuário/senha inválidos"));
    if (!u.isAtivo() || !encoder.matches(senha, u.getSenhaHash())) {
      throw new IllegalArgumentException("Usuário/senha inválidos");
    }
    String jwt = tokenProvider.gerar(u.getUsername(), u.getPerfis());
    long exp = tokenProvider.expiresAt(jwt);
    return new TokenDTO(jwt, exp);
  }
}
