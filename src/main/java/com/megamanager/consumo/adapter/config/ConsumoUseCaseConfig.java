package com.megamanager.consumo.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.consumo.application.port.in.DetalharContaClienteUseCase;
import com.megamanager.consumo.application.port.in.ListarConsumosPorClienteUseCase;
import com.megamanager.consumo.application.port.in.PagarContaClienteUseCase;
import com.megamanager.consumo.application.port.in.RegistrarConsumoUseCase;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.application.usecase.DetalharContaClienteService;
import com.megamanager.consumo.application.usecase.GerenciarConsumoService;
import com.megamanager.consumo.application.usecase.PagarContaClienteService;
import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.produto.application.port.out.ProdutoRepository;

@Configuration
public class ConsumoUseCaseConfig {

	@Bean
	public GerenciarConsumoService gerenciarConsumoService(ConsumoRepository consumoRepository,
			ClienteRepository clienteRepository, ProdutoRepository produtoRepository,
			EntradaEstoqueRepository entradaEstoqueRepository) {
		return new GerenciarConsumoService(consumoRepository, clienteRepository, produtoRepository,
				entradaEstoqueRepository);
	}

	@Bean
	public RegistrarConsumoUseCase registrarConsumoUseCase(GerenciarConsumoService service) {
		return service;
	}

	@Bean
	public ListarConsumosPorClienteUseCase listarConsumosPorClienteUseCase(GerenciarConsumoService service) {
		return service;
	}

	@Bean
	public DetalharContaClienteUseCase fecharContaClienteUseCase(ClienteRepository clienteRepository,
			ProdutoRepository produtoRepository, ConsumoRepository consumoRepository) {
		return new DetalharContaClienteService(clienteRepository, produtoRepository, consumoRepository);
	}

	@Bean
	public PagarContaClienteUseCase pagarContaClienteUseCase(ConsumoRepository consumoRepository,
			ClienteRepository clienteRepository) {
		return new PagarContaClienteService(consumoRepository, clienteRepository);
	}
}
