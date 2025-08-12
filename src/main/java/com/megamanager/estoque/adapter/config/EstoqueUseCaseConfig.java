package com.megamanager.estoque.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.estoque.application.port.in.ListarEntradasPorProdutoUseCase;
import com.megamanager.estoque.application.port.in.RegistrarEntradaUseCase;
import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.estoque.application.usecase.EntradaEstoqueService;

@Configuration
public class EstoqueUseCaseConfig {

	@Bean
	public EntradaEstoqueService estradaEstoqueService(EntradaEstoqueRepository entradaEstoqueRepository) {
		return new EntradaEstoqueService(entradaEstoqueRepository);
	}
	
	@Bean
	public RegistrarEntradaUseCase registrarEntradaUseCase(EntradaEstoqueService service) {
		return service;
	}
	
	@Bean
	public ListarEntradasPorProdutoUseCase listarEntradasPorProdutoUseCase(EntradaEstoqueService service) {
	    return service;
	}
}
