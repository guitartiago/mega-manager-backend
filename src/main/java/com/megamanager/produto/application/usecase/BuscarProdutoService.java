package com.megamanager.produto.application.usecase;

import java.util.Optional;

import com.megamanager.produto.application.port.in.BuscarProdutoUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarProdutoService implements BuscarProdutoUseCase{
	
	private final ProdutoRepository produtoRepository;

	@Override
	public Optional<Produto> buscarPorId(Long id) {
		return produtoRepository.buscarPorId(id);
	}

}
