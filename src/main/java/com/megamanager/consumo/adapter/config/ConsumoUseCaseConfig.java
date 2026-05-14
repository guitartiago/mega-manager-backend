package com.megamanager.consumo.adapter.config;

import com.megamanager.consumo.application.port.in.*;
import com.megamanager.consumo.application.usecase.*;
import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.produto.application.port.out.ProdutoRepository;

@Configuration
public class ConsumoUseCaseConfig {

    @Bean
    public AbaterEstoqueService abaterEstoqueService(EntradaEstoqueRepository entradaEstoqueRepository,
                                                     ConsumoRepository consumoRepository) {
        return new AbaterEstoqueService(entradaEstoqueRepository, consumoRepository);
    }

    @Bean
    public GerenciarConsumoService gerenciarConsumoService(ConsumoRepository consumoRepository,
                                                           ClienteRepository clienteRepository, ProdutoRepository produtoRepository,
                                                           AbaterEstoqueService abaterEstoqueService) {
        return new GerenciarConsumoService(consumoRepository, clienteRepository, produtoRepository,
                abaterEstoqueService);
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
    public DetalharContaClienteUseCase fecharContaClienteUseCase(
            ClienteRepository clienteRepository
            , ProdutoRepository produtoRepository
            , ConsumoRepository consumoRepository) {
        return new DetalharContaClienteService(clienteRepository, produtoRepository, consumoRepository);
    }

    @Bean
    public PagarContaClienteUseCase pagarContaClienteUseCase(ConsumoRepository consumoRepository,
                                                             ClienteRepository clienteRepository) {
        return new PagarContaClienteService(consumoRepository, clienteRepository);
    }

    @Bean
    public DetalharContaClienteV2UseCase detalharContaClienteV2UseCase (
            ClienteRepository clienteRepository
            , ProdutoRepository produtoRepository
            , ConsumoRepository consumoRepository
            , LancamentoRepository lancamentoRepository) {

        return new DetalharContaClienteV2Service(clienteRepository, produtoRepository
                , consumoRepository, lancamentoRepository);
    }

}
