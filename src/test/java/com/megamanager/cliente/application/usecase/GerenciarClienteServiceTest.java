package com.megamanager.cliente.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;

public class GerenciarClienteServiceTest {

    private ClienteRepository clienteRepository;
    private GerenciarClienteService service;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        service = new GerenciarClienteService(clienteRepository);
    }

    @Test
    @DisplayName("Deve cadastrar cliente e retornar com sucesso")
    void deveCadastrarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Tiago");
        cliente.setEmail("tiago@megafuzz.com");
        cliente.setPerfil(PerfilCliente.SOCIO);

        when(clienteRepository.salvar(cliente)).thenReturn(cliente);

        Cliente salvo = service.cadastrar(cliente);

        assertEquals("Tiago", salvo.getNome());
        verify(clienteRepository).salvar(cliente);
    }

    @Test
    @DisplayName("Deve retornar cliente por ID")
    void deveBuscarClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = service.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    @DisplayName("Deve retornar lista de clientes")
    void deveListarTodosClientes() {
        when(clienteRepository.listarTodos()).thenReturn(List.of(new Cliente(), new Cliente()));

        List<Cliente> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        verify(clienteRepository).listarTodos();
    }

    @Test
    @DisplayName("Deve atualizar cliente existente")
    void deveAtualizarCliente() {
        Cliente original = new Cliente();
        original.setId(1L);
        original.setNome("Tiago");

        Cliente atualizado = new Cliente();
        atualizado.setNome("Novo Nome");

        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(original));
        when(clienteRepository.salvar(any(Cliente.class))).thenReturn(atualizado);

        Optional<Cliente> resultado = service.atualizar(1L, atualizado);

        assertTrue(resultado.isPresent());
        assertEquals("Novo Nome", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve excluir cliente por ID")
    void deveExcluirCliente() {
        service.excluir(1L);
        verify(clienteRepository).excluir(1L);
    }
}
