package com.megamanager.consumo.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.consumo.domain.DadosProduto;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

class GerenciarConsumoServiceTest {

    private ConsumoRepository consumoRepository;
    private ClienteRepository clienteRepository;
    private ProdutoRepository produtoRepository;
    private AbaterEstoqueService abaterEstoqueService;

    private GerenciarConsumoService service;

    @BeforeEach
    void setup() {
        consumoRepository = mock(ConsumoRepository.class);
        clienteRepository = mock(ClienteRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        abaterEstoqueService = mock(AbaterEstoqueService.class);

        service = new GerenciarConsumoService(
                consumoRepository,
                clienteRepository,
                produtoRepository,
                abaterEstoqueService
        );
    }

    @Test
    @DisplayName("Deve registrar consumo para sócio abatendo do estoque FIFO")
    void registrarConsumoSocioComEstoque() {
        Long clienteId = 1L;
        Long produtoId = 10L;

        Cliente socio = Cliente.reconstruir(clienteId, "Tiago", "email@email.com", "11912345678", PerfilCliente.SOCIO);
        Produto produto = Produto.reconstruir(produtoId, "Cerveja", new BigDecimal("6.00"), true);

        Consumo pedido = Consumo.criar(clienteId, new DadosProduto(produtoId, 3, new BigDecimal("6.00")), LocalDateTime.now(), null);
        
        Consumo consumoEsperado = Consumo.criar(clienteId, new DadosProduto(produtoId, 1, new BigDecimal("6.00")), LocalDateTime.now(), 101L);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(socio));
        when(produtoRepository.buscarPorId(produtoId)).thenReturn(Optional.of(produto));
        when(abaterEstoqueService.abaterESalvar(any(), any(), any())).thenReturn(consumoEsperado);

        Consumo retorno = service.registrar(pedido);

        verify(abaterEstoqueService).abaterESalvar(any(), any(), any());

        assertNotNull(retorno);
        assertEquals(101L, retorno.getEntradaEstoqueId());
        assertEquals(1, retorno.getDadosProduto().getQuantidade());
    }

    @Test
    @DisplayName("Deve registrar consumo para não-sócio com preço de venda e abater estoque")
    void registrarConsumoNaoSocio() {
        Long clienteId = 2L;
        Long produtoId = 20L;

        Cliente naoSocio = Cliente.reconstruir(clienteId, "Cliente", "nao@email.com", "11912345678", PerfilCliente.COMUM);
        Produto produto = Produto.reconstruir(produtoId, "Refri", new BigDecimal("5.00"), true);
        
        Consumo consumoEsperado = Consumo.criar(clienteId, new DadosProduto(produtoId, 1, new BigDecimal("5.00")), LocalDateTime.now(), 200L);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(naoSocio));
        when(produtoRepository.buscarPorId(produtoId)).thenReturn(Optional.of(produto));
        when(abaterEstoqueService.abaterESalvar(any(), any(), any())).thenReturn(consumoEsperado);

        Consumo pedido = Consumo.criar(clienteId, new DadosProduto(produtoId, 1, new BigDecimal("5.00")), LocalDateTime.now(), null);

        Consumo retorno = service.registrar(pedido);

        verify(abaterEstoqueService).abaterESalvar(any(), any(), any());

        assertEquals(new BigDecimal("5.00"), retorno.getDadosProduto().getValorUnitario());
    }

    @Test
    @DisplayName("Deve registrar consumo mesmo sem estoque e gerar alerta")
    void registrarConsumoSemEstoque() {
        Long clienteId = 3L;
        Long produtoId = 30L;

        Cliente socio = Cliente.reconstruir(clienteId, "Zé", "ze@email.com", "11912345678", PerfilCliente.SOCIO);
        Produto produto = Produto.reconstruir(produtoId, "Água", new BigDecimal("3.00"), true);
        
        Consumo consumoEsperado = Consumo.criar(clienteId, new DadosProduto(produtoId, 2, new BigDecimal("3.00")), LocalDateTime.now(), null);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(socio));
        when(produtoRepository.buscarPorId(produtoId)).thenReturn(Optional.of(produto));
        when(abaterEstoqueService.abaterESalvar(any(), any(), any())).thenReturn(consumoEsperado);

        Consumo pedido = Consumo.criar(clienteId, new DadosProduto(produtoId, 2, new BigDecimal("3.00")), LocalDateTime.now(), null);

        Consumo retorno = service.registrar(pedido);

        verify(abaterEstoqueService).abaterESalvar(any(), any(), any());
        assertNull(retorno.getEntradaEstoqueId());
    }
}
