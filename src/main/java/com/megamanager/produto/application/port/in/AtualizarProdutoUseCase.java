package com.megamanager.produto.application.port.in;

import java.util.Optional;

import com.megamanager.produto.domain.Produto;

public interface AtualizarProdutoUseCase {
    Optional<Produto> atualizar(Long id, Produto produtoAtualizado);
}
