package com.megamanager.estoque.application.port.out;

import java.util.List;

import com.megamanager.estoque.domain.EntradaEstoque;

public interface EntradaEstoqueRepository {
	
	EntradaEstoque salvar(EntradaEstoque entradaEstoque);
	List<EntradaEstoque> listarPorProduto(Long idProduto);

}
