package com.megamanager.fechamento.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.fechamento.application.usecase.FechamentoContaService;
import com.megamanager.fechamento.application.port.out.FechamentoContaRepository;
import com.megamanager.produto.application.port.out.ProdutoRepository;

@Configuration
public class FechamentoUseCaseConfig {

    @Bean
    public FechamentoContaService fechamentoContaService(
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            ConsumoRepository consumoRepository,
            FechamentoContaRepository fechamentoRepository) {
        return new FechamentoContaService(
                clienteRepository, produtoRepository, consumoRepository, fechamentoRepository
        );
    }
}
