package com.megamanager.cliente.adapter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.megamanager.cliente.adapter.web.dto.ClienteRequestDTO;
import com.megamanager.cliente.application.port.in.AtualizarClienteUseCase;
import com.megamanager.cliente.application.port.in.BuscarClienteUseCase;
import com.megamanager.cliente.application.port.in.CadastrarClienteUseCase;
import com.megamanager.cliente.application.port.in.ExcluirClienteUseCase;
import com.megamanager.cliente.domain.PerfilCliente;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CadastrarClienteUseCase cadastrarClienteUseCase;

    @MockBean
    private BuscarClienteUseCase buscarClienteUseCase;

    @MockBean
    private AtualizarClienteUseCase atualizarClienteUseCase;

    @MockBean
    private ExcluirClienteUseCase excluirClienteUseCase;

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso (201)")
    void deveCadastrarClienteComSucesso() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNome("Tiago");
        request.setEmail("tiago@megafuzz.com");
        request.setPerfil(PerfilCliente.SOCIO);

        Mockito.when(cadastrarClienteUseCase.cadastrar(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar 200 e lista de clientes no GET /clientes")
    void deveListarTodosClientes() throws Exception {
        Mockito.when(buscarClienteUseCase.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar cliente existente por ID")
    void deveBuscarClientePorId() throws Exception {
        var cliente = new com.megamanager.cliente.domain.Cliente();
        cliente.setId(1L);
        cliente.setNome("Tiago");
        cliente.setEmail("tiago@megafuzz.com");
        cliente.setPerfil(PerfilCliente.SOCIO);

        Mockito.when(buscarClienteUseCase.buscarPorId(1L)).thenReturn(java.util.Optional.of(cliente));

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar cliente inexistente por ID")
    void deveRetornar404AoBuscarClienteInexistente() throws Exception {
        Mockito.when(buscarClienteUseCase.buscarPorId(99L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar cliente existente e retornar 200")
    void deveAtualizarClienteComSucesso() throws Exception {
        var clienteAtualizado = new com.megamanager.cliente.domain.Cliente();
        clienteAtualizado.setId(1L);
        clienteAtualizado.setNome("Tiago Atualizado");
        clienteAtualizado.setEmail("tiago@megafuzz.com");
        clienteAtualizado.setPerfil(PerfilCliente.SOCIO);

        Mockito.when(atualizarClienteUseCase.atualizar(Mockito.eq(1L), Mockito.any()))
                .thenReturn(java.util.Optional.of(clienteAtualizado));

        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome("Tiago Atualizado");
        dto.setEmail("tiago@megafuzz.com");
        dto.setPerfil(PerfilCliente.SOCIO);

        mockMvc.perform(put("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve excluir cliente com sucesso (204)")
    void deveExcluirClienteComSucesso() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }
}
