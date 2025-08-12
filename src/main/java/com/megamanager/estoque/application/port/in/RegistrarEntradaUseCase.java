package com.megamanager.estoque.application.port.in;

import com.megamanager.estoque.domain.EntradaEstoque;

public interface RegistrarEntradaUseCase {
	public EntradaEstoque resgistrar(EntradaEstoque entradaEstoque);
}
