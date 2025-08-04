package com.megamanager.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.application.port.out.ClienteRepository;
import com.megamanager.application.usecase.GerenciarClienteUseCase;

@Configuration
public class ClienteUseCaseConfig {

	@Bean
	public GerenciarClienteUseCase gerenciarClienteUseCase(ClienteRepository clienteRepository) {
	    return new GerenciarClienteUseCase(clienteRepository);
	}
}
