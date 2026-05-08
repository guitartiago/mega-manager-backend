package com.megamanager.fechamento.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import com.megamanager.consumo.domain.DadosProduto;
import com.megamanager.fechamento.application.port.out.FechamentoContaRepository;
import com.megamanager.fechamento.domain.FechamentoConta;
import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.CategoriaLancamento;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

class FechamentoComLancamentoTest {

    private FechamentoContaService service;
    private ClienteRepository clienteRepository;
    private ConsumoRepository consumoRepository;
    private LancamentoRepository lancamentoRepository;
    private FechamentoContaRepository fechamentoRepository;
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void setup() {
        clienteRepository = mock(ClienteRepository.class);
        consumoRepository = mock(ConsumoRepository.class);
        lancamentoRepository = mock(LancamentoRepository.class);
        fechamentoRepository = mock(FechamentoContaRepository.class);
        produtoRepository = mock(ProdutoRepository.class);

        service = new FechamentoContaService(
                clienteRepository,
                produtoRepository,
                consumoRepository,
                fechamentoRepository,
                lancamentoRepository
        );
    }

    @Test
    @DisplayName("Deve incluir lançamento de desconto no fechamento")
    void testarFechamentoComDesconto() {
        // Arrange
        Long clienteId = 1L;
        Cliente cliente = Cliente.reconstruir(clienteId, "João", "joao@email.com", "11987654321", PerfilCliente.COMUM);
        
        Consumo consumo = Consumo.criar(clienteId, 
                new DadosProduto(10L, 2, BigDecimal.TEN, BigDecimal.valueOf(20)),
                LocalDateTime.now(), null);
        
        Lancamento desconto = Lancamento.criar(clienteId,
                LocalDateTime.now(),
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.DESCONTO,
                new BigDecimal("5.00"),
                "Desconto fidelidade",
                "admin");

        Produto produto = Produto.reconstruir(10L, "Produto A", BigDecimal.TEN, true);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(consumoRepository.buscarNaoPagosPorCliente(clienteId)).thenReturn(List.of(consumo));
        when(lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId)).thenReturn(List.of(desconto));
        when(produtoRepository.buscarPorId(10L)).thenReturn(Optional.of(produto));
        when(fechamentoRepository.salvar(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        FechamentoConta resultado = service.fechar(clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("15.00"), resultado.getTotalPago());  // 20 - 5
        assertEquals(2, resultado.getItens().size());  // 1 consumo + 1 lançamento
        
        assertTrue(resultado.getItens().stream()
                .anyMatch(i -> "CONSUMO".equals(i.getTipoItem())));
        assertTrue(resultado.getItens().stream()
                .anyMatch(i -> "LANCAMENTO".equals(i.getTipoItem())));

        verify(lancamentoRepository).marcarLancamentosComoProcessados(any(), eq(resultado.getId()));
    }

    @Test
    @DisplayName("Deve incluir múltiplos lançamentos (débito e crédito)")
    void testarMultiplosLancamentos() {
        // Arrange
        Long clienteId = 2L;
        Cliente cliente = Cliente.reconstruir(clienteId, "Maria", "maria@email.com", "11987654321", PerfilCliente.COMUM);
        
        Consumo consumo = Consumo.criar(clienteId, 
                new DadosProduto(20L, 1, BigDecimal.valueOf(100), BigDecimal.valueOf(100)),
                LocalDateTime.now(), null);
        
        Lancamento desconto = Lancamento.criar(clienteId,
                LocalDateTime.now(),
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.DESCONTO,
                new BigDecimal("10.00"),
                "Desconto",
                "admin");

        Lancamento cobranca = Lancamento.criar(clienteId,
                LocalDateTime.now(),
                NaturezaLancamento.DEBITO,
                CategoriaLancamento.COBRANCA_ADICIONAL,
                new BigDecimal("5.00"),
                "Taxa",
                "admin");

        Produto produto = Produto.reconstruir(20L, "Produto B", BigDecimal.valueOf(100), true);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(consumoRepository.buscarNaoPagosPorCliente(clienteId)).thenReturn(List.of(consumo));
        when(lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId)).thenReturn(List.of(desconto, cobranca));
        when(produtoRepository.buscarPorId(20L)).thenReturn(Optional.of(produto));
        when(fechamentoRepository.salvar(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        FechamentoConta resultado = service.fechar(clienteId);

        // Assert
        assertNotNull(resultado);
        // 100 (consumo) - 10 (desconto) + 5 (cobrança) = 95
        assertEquals(new BigDecimal("95.00"), resultado.getTotalPago());
        assertEquals(3, resultado.getItens().size());  // 1 consumo + 2 lançamentos
    }
}