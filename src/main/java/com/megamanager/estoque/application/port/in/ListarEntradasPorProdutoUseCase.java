package com.megamanager.estoque.application.port.in;

import java.util.List;

import com.megamanager.estoque.domain.EntradaEstoque;

public interface ListarEntradasPorProdutoUseCase {
	
	 List<EntradaEstoque> listarPorProdutoId(Long produtoId);

}
