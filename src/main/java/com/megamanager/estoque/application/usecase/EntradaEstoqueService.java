package com.megamanager.estoque.application.usecase;

import java.util.List;

import com.megamanager.estoque.application.port.in.ListarEntradasPorProdutoUseCase;
import com.megamanager.estoque.application.port.in.RegistrarEntradaUseCase;
import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.estoque.domain.EntradaEstoque;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntradaEstoqueService implements RegistrarEntradaUseCase, ListarEntradasPorProdutoUseCase {
	
	private final EntradaEstoqueRepository entradaEstoqueRepository;

	@Override
	public EntradaEstoque resgistrar(EntradaEstoque entradaEstoque) {
		
		return entradaEstoqueRepository.salvar(entradaEstoque);
	}
	
	@Override
    public List<EntradaEstoque> listarPorProdutoId(Long produtoId) {
        return entradaEstoqueRepository.buscarPorProdutoId(produtoId);
    }

}
