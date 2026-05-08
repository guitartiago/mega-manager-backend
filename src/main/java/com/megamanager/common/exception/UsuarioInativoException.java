package com.megamanager.common.exception;

public class UsuarioInativoException extends DomainException {
    public UsuarioInativoException(String username) {
        super("Usuário inativo ou credenciais inválidas: " + username);
    }
}
