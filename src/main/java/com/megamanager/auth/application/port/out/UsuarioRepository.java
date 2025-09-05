package com.megamanager.auth.application.port.out;

import java.util.Optional;
import com.megamanager.auth.domain.Usuario;

public interface UsuarioRepository {
  Optional<Usuario> findByUsername(String username);
}
