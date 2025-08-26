package com.megamanager.produto.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

public class CadastrarProdutoServiceTest {
	
	private ProdutoRepository produtoRepository;
	private CadastrarProdutoService service;
	
	@BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        service = new CadastrarProdutoService(produtoRepository);
    }
	
	@Test
	@DisplayName("Deve cadastrar produto")
	void deveCadastrarProduto() {
		Produto produto = Produto.criar(
				"Cerveja Budweiser 269ml",
				new BigDecimal("6.00"));
				
		when(produtoRepository.salvar(produto)).thenReturn(produto);

        Produto salvo = service.cadastrar(produto);

        assertEquals("Cerveja Budweiser 269ml", salvo.getNome());
        verify(produtoRepository).salvar(produto);
	}
	
}