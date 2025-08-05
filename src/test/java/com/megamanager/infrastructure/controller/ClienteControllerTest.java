package com.megamanager.infrastructure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megamanager.adapter.web.controller.ClienteController;
import com.megamanager.application.usecase.GerenciarClienteUseCase;
import com.megamanager.domain.model.Cliente;
import com.megamanager.domain.model.PerfilCliente;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GerenciarClienteUseCase gerenciarClienteUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarCliente() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Tiago");
        cliente.setEmail("tiago@example.com");
        cliente.setPerfil(PerfilCliente.SOCIO);

        Mockito.when(gerenciarClienteUseCase.cadastrar(any(Cliente.class)))
                .thenReturn(cliente);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Tiago"))
                .andExpect(jsonPath("$.perfil").value("SOCIO"));
    }

    @Test
    void deveBuscarClientePorId() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Buscar");
        cliente.setEmail("buscar@example.com");
        cliente.setPerfil(PerfilCliente.COMUM);

        Mockito.when(gerenciarClienteUseCase.buscarPorId(1L))
                .thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Buscar"));
    }

    @Test
    void deveRetornar404SeClienteNaoExiste() throws Exception {
        Mockito.when(gerenciarClienteUseCase.buscarPorId(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound());
    }
}
