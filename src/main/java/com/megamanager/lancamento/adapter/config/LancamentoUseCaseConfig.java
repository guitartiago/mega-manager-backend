package com.megamanager.lancamento.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.lancamento.application.port.in.EstornarLancamentoUseCase;
import com.megamanager.lancamento.application.port.in.ListarLancamentosPorClienteUseCase;
import com.megamanager.lancamento.application.port.in.RegistrarLancamentoUseCase;
import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.application.usecase.GerenciarLancamentoService;

@Configuration
public class LancamentoUseCaseConfig {

    @Bean
    public GerenciarLancamentoService gerenciarLancamentoService(LancamentoRepository lancamentoRepository,
                                                                 ClienteRepository clienteRepository) {
        return new GerenciarLancamentoService(lancamentoRepository, clienteRepository);
    }

    @Bean
    public RegistrarLancamentoUseCase registrarLancamentoUseCase(GerenciarLancamentoService service) {
        return service;
    }

    @Bean
    public ListarLancamentosPorClienteUseCase listarLancamentosPorClienteUseCase(GerenciarLancamentoService service) {
        return service;
    }

    @Bean
    public EstornarLancamentoUseCase estornarLancamentoUseCase(GerenciarLancamentoService service) {
        return service;
    }
}
