package com.megamanager.fechamento.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.fechamento.application.port.in.BuscarFechamentoUseCase;
import com.megamanager.fechamento.application.port.in.FecharContaClienteUseCase;
import com.megamanager.fechamento.application.port.in.ListarFechamentosUseCase;
import com.megamanager.fechamento.application.port.out.FechamentoContaRepository;
import com.megamanager.fechamento.domain.FechamentoConta;
import com.megamanager.fechamento.domain.ItemFechamento;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FechamentoContaService implements
        FecharContaClienteUseCase,
        ListarFechamentosUseCase,
        BuscarFechamentoUseCase {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ConsumoRepository consumoRepository;
    private final FechamentoContaRepository fechamentoRepository;

    @Override
    @Transactional
    public FechamentoConta fechar(Long clienteId) {
        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        List<Consumo> abertos = consumoRepository.buscarNaoPagosPorCliente(clienteId);
        if (abertos.isEmpty()) {
            throw new IllegalStateException("Cliente não possui consumos em aberto");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "sistema";

        List<ItemFechamento> itens = abertos.stream().map(consumo -> {
            Produto produto = produtoRepository.buscarPorId(consumo.getDadosProduto().getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

            BigDecimal valorUnitario = calcularValorUnitario(cliente, consumo, produto);
            BigDecimal valorTotal = valorUnitario.multiply(
                    BigDecimal.valueOf(consumo.getDadosProduto().getQuantidade()));

            return ItemFechamento.builder()
                    .produtoId(produto.getId())
                    .nomeProduto(produto.getNome())
                    .quantidade(consumo.getDadosProduto().getQuantidade())
                    .valorUnitario(valorUnitario)
                    .valorTotal(valorTotal)
                    .build();
        }).toList();

        BigDecimal total = itens.stream()
                .map(ItemFechamento::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        FechamentoConta fechamento = FechamentoConta.builder()
                .clienteId(cliente.getId())
                .clienteNome(cliente.getNome())
                .usuarioUsername(username)
                .dataHora(LocalDateTime.now())
                .totalPago(total)
                .itens(itens)
                .build();

        FechamentoConta salvo = fechamentoRepository.salvar(fechamento);

        consumoRepository.marcarConsumosComoPagos(clienteId);

        return salvo;
    }

    @Override
    public List<FechamentoConta> listar(Long clienteId, LocalDateTime de, LocalDateTime ate) {
        return fechamentoRepository.listar(clienteId, de, ate);
    }

    @Override
    public Optional<FechamentoConta> buscarPorId(Long fechamentoId) {
        return fechamentoRepository.buscarPorId(fechamentoId);
    }

    private BigDecimal calcularValorUnitario(Cliente cliente, Consumo consumo, Produto produto) {
        if (cliente.getPerfil() == PerfilCliente.SOCIO && consumo.getDadosProduto().getValorUnitario() != null) {
            return consumo.getDadosProduto().getValorUnitario();
        } else {
            return produto.getPrecoVenda();
        }
    }
}
