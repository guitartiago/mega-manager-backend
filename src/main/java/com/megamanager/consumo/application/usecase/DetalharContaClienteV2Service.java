package com.megamanager.consumo.application.usecase;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.adapter.web.dto.DetalheContaDTO;
import com.megamanager.consumo.adapter.web.dto.ItemConsumoDTO;
import com.megamanager.consumo.adapter.web.dto.LancamentoContaDTO;
import com.megamanager.consumo.application.port.in.DetalharContaClienteV2UseCase;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import java.math.BigDecimal;
import java.util.List;

public class DetalharContaClienteV2Service implements DetalharContaClienteV2UseCase {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ConsumoRepository consumoRepository;
    private final LancamentoRepository lancamentoRepository;

    public DetalharContaClienteV2Service(ClienteRepository clienteRepository,
                                       ProdutoRepository produtoRepository,
                                       ConsumoRepository consumoRepository,
                                       LancamentoRepository lancamentoRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.consumoRepository = consumoRepository;
        this.lancamentoRepository  = lancamentoRepository;
    }

    @Override
    public DetalheContaDTO execute(Long clienteId) {

        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        List<Consumo> consumosAbertos = consumoRepository.buscarNaoPagosPorCliente(clienteId);

        List<Lancamento> lancamentosAbertos = lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId);
        
        List<ItemConsumoDTO> itensConsumoDTO = getItensConsumoDTO(consumosAbertos, cliente);

        BigDecimal valorTotalConsumo = itensConsumoDTO.stream()
                .map(ItemConsumoDTO::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<LancamentoContaDTO> lancamentosContaDTO = getLancamentosContaDTO(clienteId, lancamentosAbertos);

        BigDecimal valorTotalLancamento = lancamentosContaDTO.stream()
                .map(LancamentoContaDTO::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalConta = valorTotalConsumo.add(valorTotalLancamento);

        return DetalheContaDTO.builder()
                .clienteId(clienteId)
                .nomeCliente(cliente.getNome())
                .perfil(cliente.getPerfil().name())
                .itens(itensConsumoDTO)
                .totalConsumos(valorTotalConsumo)
                .lancamentos(lancamentosContaDTO)
                .totalLancamentos(valorTotalLancamento)
                .total(valorTotalConta).build();

    }

    private static List<LancamentoContaDTO> getLancamentosContaDTO(Long clienteId, List<Lancamento> lancamentosAbertos) {
        return lancamentosAbertos.stream().map(lancamento -> {
            BigDecimal valor = lancamento.getNatureza() == NaturezaLancamento.DEBITO
                    ? lancamento.getValor()                   // Positivo para débito
                    : lancamento.getValor().negate();         // Negativo para crédito

            return LancamentoContaDTO.builder()
                    .id(lancamento.getId())
                    .clienteId(clienteId)
                    .dataHora(lancamento.getDataHora())
                    .natureza(lancamento.getNatureza())
                    .categoria(lancamento.getCategoria().name())
                    .valor(valor)
                    .motivo(lancamento.getMotivo())
                    .responsavelUsername(lancamento.getResponsavelUsername())
                    .aberto(true)
                    .build();
        }).toList();
    }

    private List<ItemConsumoDTO> getItensConsumoDTO(List<Consumo> consumosAbertos, Cliente cliente) {
        return consumosAbertos.stream().map(consumo -> {
            Produto produto = produtoRepository.buscarPorId(consumo.getDadosProduto().getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

            BigDecimal valorUnitario = calcularValorUnitario(cliente, consumo, produto);
            BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(consumo.getDadosProduto().getQuantidade()));

            return ItemConsumoDTO.builder()
                    .nomeProduto(produto.getNome())
                    .quantidade(consumo.getDadosProduto().getQuantidade())
                    .valorUnitario(valorUnitario)
                    .valorTotal(valorTotal)
                    .dataHora(consumo.getDataHora())
                    .build();
        }).toList();
    }

    private BigDecimal calcularValorUnitario(Cliente cliente, Consumo consumo, Produto produto) {
        if (cliente.getPerfil() == PerfilCliente.SOCIO && consumo.getDadosProduto().getValorUnitario() != null) {
            return consumo.getDadosProduto().getValorUnitario(); // custo do lote (armazenado no consumo)
        } else {
            return produto.getPrecoVenda();
        }
    }

}
