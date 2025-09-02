package com.megamanager.consumo.application.usecase;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.dto.ExtratoContaCliente;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.consumo.domain.DadosProduto;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FecharContaClienteServiceTest {

    private ClienteRepository clienteRepository;
    private ProdutoRepository produtoRepository;
    private ConsumoRepository consumoRepository;

    private FecharContaClienteService service;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        consumoRepository = mock(ConsumoRepository.class);
        service = new FecharContaClienteService(clienteRepository, produtoRepository, consumoRepository);
    }

    @Test
    @DisplayName("Deve gerar extrato para cliente não sócio com preço de venda do produto")
    void deveGerarExtratoParaClienteNaoSocio() {
        // Arrange
        Long clienteId = 1L;
        Long produtoId = 10L;
        Produto produto = Produto.criar("Cerveja Original", new BigDecimal("6.00"));
        Cliente cliente = Cliente.reconstruir(clienteId, "Tiago", "tiago@email.com", PerfilCliente.COMUM);
        DadosProduto dados = new DadosProduto(produtoId, 2, new BigDecimal("6.00")); // valorUnitario do consumo não será usado
        Consumo consumo = Consumo.reconstruir(1L, clienteId, dados, LocalDateTime.now(), false, null);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(produtoRepository.buscarPorId(produtoId)).thenReturn(Optional.of(produto));
        when(consumoRepository.buscarNaoPagosPorCliente(clienteId)).thenReturn(List.of(consumo));

        // Act
        ExtratoContaCliente extrato = service.fecharConta(clienteId);

        // Assert
        assertThat(extrato).isNotNull();
        assertThat(extrato.getClienteId()).isEqualTo(clienteId);
        assertThat(extrato.getPerfil()).isEqualTo(PerfilCliente.COMUM);
        assertThat(extrato.getItens()).hasSize(1);
        assertThat(extrato.getItens().get(0).getValorUnitario()).isEqualTo(new BigDecimal("6.00"));
        assertThat(extrato.getItens().get(0).getValorTotal()).isEqualTo(new BigDecimal("12.00"));
        assertThat(extrato.getTotal()).isEqualTo(new BigDecimal("12.00"));
    }

    @Test
    @DisplayName("Deve gerar extrato para sócio com valor de custo armazenado no consumo")
    void deveGerarExtratoParaClienteSocio() {
        Long clienteId = 2L;
        Long produtoId = 20L;
        Produto produto = Produto.criar("Heineken", new BigDecimal("8.00")); // não será usado para sócio
        Cliente cliente = Cliente.reconstruir(clienteId, "Fábio", "fabio@email.com", PerfilCliente.SOCIO);
        DadosProduto dados = new DadosProduto(produtoId, 3, new BigDecimal("2.50")); // custo
        Consumo consumo = Consumo.reconstruir(2L, clienteId, dados, LocalDateTime.now(), false, null);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(produtoRepository.buscarPorId(produtoId)).thenReturn(Optional.of(produto));
        when(consumoRepository.buscarNaoPagosPorCliente(clienteId)).thenReturn(List.of(consumo));

        ExtratoContaCliente extrato = service.fecharConta(clienteId);

        assertThat(extrato.getPerfil()).isEqualTo(PerfilCliente.SOCIO);
        assertThat(extrato.getItens()).hasSize(1);
        assertThat(extrato.getItens().get(0).getValorUnitario()).isEqualTo(new BigDecimal("2.50"));
        assertThat(extrato.getItens().get(0).getValorTotal()).isEqualTo(new BigDecimal("7.50"));
        assertThat(extrato.getTotal()).isEqualTo(new BigDecimal("7.50"));
    }
}
