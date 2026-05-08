package com.megamanager.common.exception;

public class ClienteNaoEncontradoException extends DomainException {
    public ClienteNaoEncontradoException(Long id) {
        super("Cliente não encontrado com ID: " + id);
    }

    public ClienteNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

