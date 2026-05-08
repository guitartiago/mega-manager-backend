package com.megamanager.produto.application.usecase;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;

import com.megamanager.produto.application.port.in.AtualizarProdutoUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AtualizarProdutoService implements AtualizarProdutoUseCase {
	
	private final ProdutoRepository produtoRepository;
	
	@Override
	@CacheEvict(value = "produtos", key = "#id")
	public Optional<Produto> atualizar(Long id, Produto produtoAtualizado) {
		log.info("Atualizando produto com ID [{}]", id);
		return produtoRepository.buscarPorId(id).map(produtoExistente -> {
			produtoAtualizado.setId(id);
			Produto atualizado = produtoRepository.salvar(produtoAtualizado);
			log.info("Produto [{}] atualizado com sucesso", id);
			return atualizado;
		});
	}
}
