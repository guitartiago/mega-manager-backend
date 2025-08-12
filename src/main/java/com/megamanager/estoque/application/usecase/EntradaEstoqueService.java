package com.megamanager.estoque.application.usecase;

import com.megamanager.estoque.application.port.in.RegistrarEntradaUseCase;
import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.estoque.domain.EntradaEstoque;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntradaEstoqueService implements RegistrarEntradaUseCase {
	
	private final EntradaEstoqueRepository entradaEstoqueRepository;

	@Override
	public EntradaEstoque resgistrar(EntradaEstoque entradaEstoque) {
		
		return entradaEstoqueRepository.salvar(entradaEstoque);
	}

}
