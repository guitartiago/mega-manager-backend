package com.megamanager.produto.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamanager.produto.application.port.in.CadastrarProdutoUseCase;
import com.megamanager.produto.application.port.in.ListarProdutosUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.application.usecase.CadastrarProdutoService;
import com.megamanager.produto.application.usecase.ListarProdutosService;

@Configuration
public class ProdutoUseCaseConfig {

    @Bean
    public CadastrarProdutoService produtoService(ProdutoRepository produtoRepository) {
        return new CadastrarProdutoService(produtoRepository);
    }
    
    @Bean
    public ListarProdutosService listarProdutoService(ProdutoRepository produtoRepository) {
        return new ListarProdutosService(produtoRepository);
    }

    @Bean
    public CadastrarProdutoUseCase cadastrarProdutoUseCase(CadastrarProdutoService service) {
        return service;
    }

    @Bean
    public ListarProdutosUseCase listarProdutosUseCase(ListarProdutosService service) {
        return service;
    }
}
