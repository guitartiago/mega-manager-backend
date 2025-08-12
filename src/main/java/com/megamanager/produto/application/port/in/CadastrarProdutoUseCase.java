package com.megamanager.produto.application.port.in;

import com.megamanager.produto.domain.Produto;

public interface CadastrarProdutoUseCase {
    Produto cadastrar(Produto produto);
}
