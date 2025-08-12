package com.megamanager.produto.application.usecase;

import java.util.List;

import com.megamanager.produto.application.port.in.ListarProdutosUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarProdutosService implements ListarProdutosUseCase {
	
	private final ProdutoRepository produtoRepository;
	
	@Override
    public List<Produto> listarTodos() {
        return produtoRepository.listarTodos();
    }

}
