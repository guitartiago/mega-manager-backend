package com.megamanager.common.exception;

public class EstoqueInsuficienteException extends DomainException {
    public EstoqueInsuficienteException(Long produtoId, int solicitado, int disponivel) {
        super(String.format(
            "Estoque insuficiente para produto %d. Solicitado: %d, Disponível: %d",
            produtoId, solicitado, disponivel
        ));
    }

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}

