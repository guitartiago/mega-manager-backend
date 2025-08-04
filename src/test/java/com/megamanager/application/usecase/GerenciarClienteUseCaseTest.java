package com.megamanager.application.usecase;

import com.megamanager.application.port.out.ClienteRepository;
import com.megamanager.domain.model.Cliente;
import com.megamanager.domain.model.PerfilCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GerenciarClienteUseCaseTest {

    private ClienteRepository clienteRepository;
    private GerenciarClienteUseCase useCase;

    @BeforeEach
    void setup() {
        clienteRepository = Mockito.mock(ClienteRepository.class);
        useCase = new GerenciarClienteUseCase(clienteRepository);
    }

    @Test
    void deveCadastrarCliente() {
        Cliente novo = new Cliente();
        novo.setNome("Tiago");
        novo.setEmail("tiago@example.com");
        novo.setPerfil(PerfilCliente.SOCIO);

        Cliente salvo = new Cliente();
        salvo.setId(1L);
        salvo.setNome("Tiago");
        salvo.setEmail("tiago@example.com");
        salvo.setPerfil(PerfilCliente.SOCIO);

        when(clienteRepository.salvar(novo)).thenReturn(salvo);

        Cliente resultado = useCase.cadastrar(novo);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Tiago");
        verify(clienteRepository).salvar(novo);
    }

    @Test
    void deveBuscarClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setId(10L);
        cliente.setNome("Buscar");
        cliente.setEmail("buscar@example.com");
        cliente.setPerfil(PerfilCliente.COMUM);

        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = useCase.buscarPorId(10L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Buscar");
    }

    @Test
    void deveListarClientes() {
        Cliente c1 = new Cliente();
        c1.setNome("C1");

        Cliente c2 = new Cliente();
        c2.setNome("C2");

        when(clienteRepository.listarTodos()).thenReturn(Arrays.asList(c1, c2));

        List<Cliente> lista = useCase.listarTodos();

        assertThat(lista).hasSize(2);
        verify(clienteRepository).listarTodos();
    }

    @Test
    void deveAtualizarClienteExistente() {
        Cliente existente = new Cliente();
        existente.setId(5L);
        existente.setNome("Antigo");

        Cliente novo = new Cliente();
        novo.setNome("Novo");

        Cliente atualizado = new Cliente();
        atualizado.setId(5L);
        atualizado.setNome("Novo");

        when(clienteRepository.buscarPorId(5L)).thenReturn(Optional.of(existente));
        when(clienteRepository.salvar(any(Cliente.class))).thenReturn(atualizado);

        Optional<Cliente> resultado = useCase.atualizar(5L, novo);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Novo");
    }

    @Test
    void deveExcluirCliente() {
        useCase.excluir(99L);
        verify(clienteRepository).excluir(99L);
    }
}
