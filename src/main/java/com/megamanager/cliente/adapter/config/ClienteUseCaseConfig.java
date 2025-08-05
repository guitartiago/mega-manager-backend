package com.megamanager.cliente.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.cliente.application.port.in.AtualizarClienteUseCase;
import com.megamanager.cliente.application.port.in.BuscarClienteUseCase;
import com.megamanager.cliente.application.port.in.CadastrarClienteUseCase;
import com.megamanager.cliente.application.port.in.ExcluirClienteUseCase;
import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.application.usecase.GerenciarClienteService;

@Configuration
public class ClienteUseCaseConfig {

    @Bean
    public GerenciarClienteService gerenciarClienteService(ClienteRepository clienteRepository) {
        return new GerenciarClienteService(clienteRepository);
    }

    @Bean
    public CadastrarClienteUseCase cadastrarClienteUseCase(GerenciarClienteService service) {
        return service;
    }

    @Bean
    public BuscarClienteUseCase buscarClienteUseCase(GerenciarClienteService service) {
        return service;
    }

    @Bean
    public AtualizarClienteUseCase atualizarClienteUseCase(GerenciarClienteService service) {
        return service;
    }

    @Bean
    public ExcluirClienteUseCase excluirClienteUseCase(GerenciarClienteService service) {
        return service;
    }
}
