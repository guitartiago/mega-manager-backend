package com.megamanager.produto.adapter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megamanager.produto.adapter.web.dto.ProdutoRequestDTO;
import com.megamanager.produto.application.port.in.CadastrarProdutoUseCase;
import com.megamanager.produto.application.port.in.ListarProdutosUseCase;

@WebMvcTest(ProdutoController.class)
public class ProdutoControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
	@MockBean
    private CadastrarProdutoUseCase cadastrarProdutoUseCase;
	
	@MockBean
	private ListarProdutosUseCase listarProdutoUseCase;
	
	@Test
    @DisplayName("Deve cadastrar produto com sucesso (201)")
    void deveCadastrarProdutoComSucesso() throws Exception {
        ProdutoRequestDTO request = new ProdutoRequestDTO();
        
        request.setNome("Cerveja Budweiser 269ml");
        request.setPrecoVenda(new BigDecimal("6.00"));
        
        Mockito.when(cadastrarProdutoUseCase.cadastrar(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
	
	@Test
    @DisplayName("Deve cadastrar produto com sucesso (201)")
    void deveCadastrarListaDeProdutosComSucesso() throws Exception {
        List<ProdutoRequestDTO> request = mockCadastrarListaProdutosRequest();
        
        Mockito.when(cadastrarProdutoUseCase.cadastrar(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/produtos/lote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
	
	@Test
    @DisplayName("Deve retornar 200 e lista de clientes no GET /clientes")
    void deveListarTodosProdutos() throws Exception {
        Mockito.when(listarProdutoUseCase.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk());
    }
	
	private List<ProdutoRequestDTO> mockCadastrarListaProdutosRequest() {
		ProdutoRequestDTO produtoRequest1 = new ProdutoRequestDTO();
        ProdutoRequestDTO produtoRequest2 = new ProdutoRequestDTO();
        
        produtoRequest1.setNome("Cerveja Budweiser 269ml");
        produtoRequest1.setPrecoVenda(new BigDecimal("6.00"));
        produtoRequest2.setNome("Cerveja Budweiser 269ml");
        produtoRequest2.setPrecoVenda(new BigDecimal("6.00"));
        
        List<ProdutoRequestDTO> request = new ArrayList<ProdutoRequestDTO>();
        request.add(produtoRequest1);
        request.add(produtoRequest2);
		return request;
	}

}
