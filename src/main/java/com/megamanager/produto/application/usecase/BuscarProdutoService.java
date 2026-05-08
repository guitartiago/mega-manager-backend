package com.megamanager.produto.application.usecase;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;

import com.megamanager.produto.application.port.in.BuscarProdutoUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BuscarProdutoService implements BuscarProdutoUseCase {
	
	private final ProdutoRepository produtoRepository;

	@Override
	@Cacheable(value = "produtos", key = "#id", unless = "#result == null")
	public Optional<Produto> buscarPorId(Long id) {
		log.debug("Buscando produto com ID [{}]", id);
		return produtoRepository.buscarPorId(id);
	}

}
