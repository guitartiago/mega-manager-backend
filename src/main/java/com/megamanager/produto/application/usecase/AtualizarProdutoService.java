package com.megamanager.produto.application.usecase;

import java.util.Optional;

import com.megamanager.produto.application.port.in.AtualizarProdutoUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarProdutoService  implements AtualizarProdutoUseCase{
	
	private final ProdutoRepository produtoRepository;
	
	@Override
	public Optional<Produto> atualizar(Long id, Produto produtoAtualizado) {
		return produtoRepository.buscarPorId(id).map(produtoExstente -> {
			produtoAtualizado.setId(id);
			return produtoRepository.salvar(produtoAtualizado);
		});
	}

}
