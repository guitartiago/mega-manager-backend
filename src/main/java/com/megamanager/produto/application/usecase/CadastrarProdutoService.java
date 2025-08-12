package com.megamanager.produto.application.usecase;

import com.megamanager.produto.application.port.in.CadastrarProdutoUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CadastrarProdutoService implements CadastrarProdutoUseCase {

    private final ProdutoRepository produtoRepository;

    @Override
    public Produto cadastrar(Produto produto) {
        return produtoRepository.salvar(produto);
    }
}
