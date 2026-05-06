package com.megamanager.common.exception;

public class ProdutoNaoEncontradoException extends DomainException {
    public ProdutoNaoEncontradoException(Long id) {
        super("Produto não encontrado com ID: " + id);
    }

    public ProdutoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

