package com.megamanager.consumo.application.usecase;

import java.math.BigDecimal;
import java.util.List;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.dto.ExtratoContaCliente;
import com.megamanager.consumo.application.dto.ItemExtratoDTO;
import com.megamanager.consumo.application.port.in.DetalharContaClienteUseCase;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

public class DetalharContaClienteService implements DetalharContaClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ConsumoRepository consumoRepository;

    public DetalharContaClienteService(ClienteRepository clienteRepository,
                                     ProdutoRepository produtoRepository,
                                     ConsumoRepository consumoRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.consumoRepository = consumoRepository;
    }

    @Override
    public ExtratoContaCliente detalharConta(Long clienteId) {
        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        List<Consumo> consumos = consumoRepository.buscarNaoPagosPorCliente(clienteId);

        List<ItemExtratoDTO> itens = consumos.stream().map(consumo -> {
            Produto produto = produtoRepository.buscarPorId(consumo.getDadosProduto().getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

            BigDecimal valorUnitario = calcularValorUnitario(cliente, consumo, produto);
            BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(consumo.getDadosProduto().getQuantidade()));

            return ItemExtratoDTO.builder()
                    .nomeProduto(produto.getNome())
                    .quantidade(consumo.getDadosProduto().getQuantidade())
                    .valorUnitario(valorUnitario)
                    .valorTotal(valorTotal)
                    .dataHora(consumo.getDataHora())
                    .build();
        }).toList();

        BigDecimal total = itens.stream()
                .map(ItemExtratoDTO::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ExtratoContaCliente.builder()
                .clienteId(cliente.getId())
                .nomeCliente(cliente.getNome())
                .perfil(cliente.getPerfil())
                .itens(itens)
                .total(total)
                .build();
    }

    private BigDecimal calcularValorUnitario(Cliente cliente, Consumo consumo, Produto produto) {
        if (cliente.getPerfil() == PerfilCliente.SOCIO && consumo.getDadosProduto().getValorUnitario() != null) {
            return consumo.getDadosProduto().getValorUnitario(); // custo do lote (armazenado no consumo)
        } else {
            return produto.getPrecoVenda();
        }
    }
}
