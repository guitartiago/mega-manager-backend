package com.megamanager.common.exception;

public class EntradaEstoqueNaoEncontradaException extends DomainException {
    public EntradaEstoqueNaoEncontradaException(Long id) {
        super("Entrada de estoque não encontrada com ID: " + id);
    }
}

