package com.megamanager.produto.application.usecase;

import org.springframework.cache.annotation.CacheEvict;

import com.megamanager.produto.application.port.in.CadastrarProdutoUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CadastrarProdutoService implements CadastrarProdutoUseCase {

    private final ProdutoRepository produtoRepository;

    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    public Produto cadastrar(Produto produto) {
        log.info("Cadastrando novo produto [{}]", produto.getNome());
        Produto salvo = produtoRepository.salvar(produto);
        log.info("Produto cadastrado com sucesso - ID [{}]", salvo.getId());
        return salvo;
    }
}
