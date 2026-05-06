package com.megamanager.cliente.adapter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.megamanager.auth.application.port.out.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes do ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

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
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nome("Tiago Silva")
                .email("tiago@megafuzz.com")
                .celular("11992376458")
                .perfil(PerfilCliente.SOCIO)
                .build();

        Cliente cliente = Cliente.reconstruir(1L, "Tiago Silva", "tiago@megafuzz.com", "11992376458", PerfilCliente.SOCIO);
        Mockito.when(cadastrarClienteUseCase.cadastrar(Mockito.any())).thenReturn(cliente);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve retornar 400 quando nome está vazio")
    void deveRetornar400ComNomeVazio() throws Exception {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nome("")
                .email("tiago@megafuzz.com")
                .celular("11992376458")
                .perfil(PerfilCliente.SOCIO)
                .build();

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação dos dados"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando email é inválido")
    void deveRetornar400ComEmailInvalido() throws Exception {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nome("Tiago Silva")
                .email("email-invalido")
                .celular("11992376458")
                .perfil(PerfilCliente.SOCIO)
                .build();

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 200 com lista paginada de clientes no GET /clientes")
    void deveListarClientesComPaginacao() throws Exception {
        Cliente cliente1 = Cliente.reconstruir(1L, "Cliente 1", "cliente1@test.com", "11999999999", PerfilCliente.COMUM);
        Cliente cliente2 = Cliente.reconstruir(2L, "Cliente 2", "cliente2@test.com", "11999999998", PerfilCliente.SOCIO);
        
        Mockito.when(buscarClienteUseCase.listarTodos())
                .thenReturn(List.of(cliente1, cliente2));

        mockMvc.perform(get("/clientes?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar cliente existente por ID")
    void deveBuscarClientePorId() throws Exception {
        Cliente cliente = Cliente.reconstruir(1L, "Tiago", "tiago@megamanager.com", "11992376458", PerfilCliente.SOCIO);
        Mockito.when(buscarClienteUseCase.buscarPorId(1L)).thenReturn(java.util.Optional.of(cliente));

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Tiago"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar cliente inexistente por ID")
    void deveRetornar404AoBuscarClienteInexistente() throws Exception {
        Mockito.when(buscarClienteUseCase.buscarPorId(99L))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar cliente existente e retornar 200")
    void deveAtualizarClienteComSucesso() throws Exception {
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("Tiago Atualizado")
                .email("tiago.novo@megafuzz.com")
                .celular("11992376458")
                .perfil(PerfilCliente.SOCIO)
                .build();

        Cliente clienteAtualizado = Cliente.reconstruir(1L, "Tiago Atualizado", "tiago.novo@megafuzz.com", 
                "11992376458", PerfilCliente.SOCIO);
        
        Mockito.when(atualizarClienteUseCase.atualizar(Mockito.eq(1L), Mockito.any()))
                .thenReturn(java.util.Optional.of(clienteAtualizado));

        mockMvc.perform(put("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Tiago Atualizado"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar cliente inexistente")
    void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("Novo Nome")
                .email("novo@test.com")
                .celular("11999999999")
                .perfil(PerfilCliente.COMUM)
                .build();

        Mockito.when(atualizarClienteUseCase.atualizar(Mockito.eq(99L), Mockito.any()))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(put("/clientes/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve excluir cliente com sucesso (204)")
    void deveExcluirClienteComSucesso() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }
}
