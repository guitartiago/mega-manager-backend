package com.megamanager.produto.application.port.in;

import java.util.Optional;

import com.megamanager.produto.domain.Produto;

public interface BuscarProdutoUseCase {
    Optional<Produto> buscarPorId(Long id);
}
