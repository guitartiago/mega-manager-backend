package com.megamanager.consumo.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.estoque.domain.EntradaEstoque;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

class GerenciarConsumoServiceTest {

    private ConsumoRepository consumoRepository;
    private ClienteRepository clienteRepository;
    private ProdutoRepository produtoRepository;
    private EntradaEstoqueRepository entradaEstoqueRepository;

    private GerenciarConsumoService service;

    @BeforeEach
    void setup() {
        consumoRepository = mock(ConsumoRepository.class);
        clienteRepository = mock(ClienteRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        entradaEstoqueRepository = mock(EntradaEstoqueRepository.class);

        service = new GerenciarConsumoService(
                consumoRepository,
                clienteRepository,
                produtoRepository,
                entradaEstoqueRepository
        );
    }

    @Test
    @DisplayName("Deve registrar consumo para sócio abatendo do estoque FIFO")
    void registrarConsumoSocioComEstoque() {
        Long clienteId = 1L;
        Long produtoId = 10L;

        Cliente socio = Cliente.reconstruir(clienteId, "Tiago", "email@email.com", PerfilCliente.SOCIO);
        Produto produto = Produto.reconstruir(produtoId, "Cerveja", new BigDecimal("6.00"), true);

        EntradaEstoque entrada1 = EntradaEstoque.reconstruir(100L, produtoId, 2, new BigDecimal("2.50"), LocalDateTime.now().minusDays(2), 2);
        EntradaEstoque entrada2 = EntradaEstoque.reconstruir(101L, produtoId, 3, new BigDecimal("2.70"), LocalDateTime.now().minusDays(1), 3);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(socio));
        when(produtoRepository.buscarPorId(produtoId)).thenReturn(Optional.of(produto));
        when(entradaEstoqueRepository.buscarPorProdutoId(produtoId)).thenReturn(List.of(entrada1, entrada2));

        Consumo pedido = Consumo.criar(clienteId, produtoId, 3, new BigDecimal("6.00"), LocalDateTime.now(), null);

        when(consumoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Consumo retorno = service.registrar(pedido);

        verify(entradaEstoqueRepository, times(2)).salvar(any());
        verify(consumoRepository, times(2)).salvar(any());

        assertNotNull(retorno);
        assertEquals(101L, retorno.getEntradaEstoqueId()); // deve ser o último lote usado
        assertEquals(1, retorno.getQuantidade());
    }

    @Test
    @DisplayName("Deve registrar consumo para não-sócio com preço de venda e abater estoque")
    void registrarConsumoNaoSocio() {
        Long clienteId = 2L;
        Long produtoId = 20L;

        Cliente naoSocio = Cliente.reconstruir(clienteId, "Cliente", "nao@email.com", PerfilCliente.COMUM);
        Produto produto = Produto.reconstruir(produtoId, "Refri", new BigDecimal("5.00"), true);
        EntradaEstoque entrada = EntradaEstoque.reconstruir(200L, produtoId, 2, new BigDecimal("2.00"), LocalDateTime.now().minusDays(1), 2);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(naoSocio));
        when(produtoRepository.buscarPorId(produtoId)).thenReturn(Optional.of(produto));
        when(entradaEstoqueRepository.buscarPorProdutoId(produtoId)).thenReturn(List.of(entrada));
        when(consumoRepository.salvar(any())).thenAnswer(i -> i.getArgument(0));

        Consumo pedido = Consumo.criar(clienteId, produtoId, 1, new BigDecimal("5.00"), LocalDateTime.now(), null);

        Consumo retorno = service.registrar(pedido);

        verify(consumoRepository).salvar(any());
        verify(entradaEstoqueRepository).salvar(any());

        assertEquals(new BigDecimal("5.00"), retorno.getValorUnitario());
    }

    @Test
    @DisplayName("Deve registrar consumo mesmo sem estoque e gerar alerta")
    void registrarConsumoSemEstoque() {
        Long clienteId = 3L;
        Long produtoId = 30L;

        Cliente socio = Cliente.reconstruir(clienteId, "Zé", "ze@email.com", PerfilCliente.SOCIO);
        Produto produto = Produto.reconstruir(produtoId, "Água", new BigDecimal("3.00"), true);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(socio));
        when(produtoRepository.buscarPorId(produtoId)).thenReturn(Optional.of(produto));
        when(entradaEstoqueRepository.buscarPorProdutoId(produtoId)).thenReturn(List.of());
        when(consumoRepository.salvar(any())).thenAnswer(i -> i.getArgument(0));

        Consumo pedido = Consumo.criar(clienteId, produtoId, 2, new BigDecimal("3.00"), LocalDateTime.now(), null);

        Consumo retorno = service.registrar(pedido);

        verify(consumoRepository).salvar(any());
        assertNull(retorno.getEntradaEstoqueId());
    }
}
