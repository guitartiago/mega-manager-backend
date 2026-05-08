package com.megamanager.fechamento.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FechamentoContaService implements
        FecharContaClienteUseCase,
        ListarFechamentosUseCase,
        BuscarFechamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(FechamentoContaService.class);

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ConsumoRepository consumoRepository;
    private final FechamentoContaRepository fechamentoRepository;
    private final LancamentoRepository lancamentoRepository;

    @Override
    @Transactional
    public FechamentoConta fechar(Long clienteId) {
        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        List<Consumo> consumosAbertos = consumoRepository.buscarNaoPagosPorCliente(clienteId);

        List<Lancamento> lancamentosAbertos = lancamentoRepository.buscarNaoProcessadosPorCliente(clienteId);


        if (consumosAbertos.isEmpty() && lancamentosAbertos.isEmpty()) {
            throw new IllegalStateException("Cliente não possui consumos ou lançamentos em aberto");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "sistema";

        List<ItemFechamento> itens = new ArrayList<>();

        consumosAbertos.stream().map(consumo -> {
            Produto produto = produtoRepository.buscarPorId(consumo.getDadosProduto().getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

            BigDecimal valorUnitario = calcularValorUnitario(cliente, consumo, produto);
            BigDecimal valorTotal = valorUnitario.multiply(
                    BigDecimal.valueOf(consumo.getDadosProduto().getQuantidade()));

            return ItemFechamento.builder()
                    .tipoItem("CONSUMO")
                    .produtoId(produto.getId())
                    .nomeProduto(produto.getNome())
                    .quantidade(consumo.getDadosProduto().getQuantidade())
                    .valorUnitario(valorUnitario)
                    .valorTotal(valorTotal)
                    .build();
        }).forEach(itens::add);

        lancamentosAbertos.stream().map(lancamento -> {
            BigDecimal valor = lancamento.getNatureza() == NaturezaLancamento.DEBITO
                    ? lancamento.getValor()                   // Positivo para débito
                    : lancamento.getValor().negate();         // Negativo para crédito

            String descricao = lancamento.getCategoria().name() + ": " +
                    (lancamento.getMotivo() != null ? lancamento.getMotivo() : "");

            log.debug("Incluindo lançamento no fechamento: {} - {}",
                    lancamento.getId(), descricao);

            return ItemFechamento.builder()
                    .tipoItem("LANCAMENTO")
                    .lancamentoId(lancamento.getId())
                    .descricao(descricao)
                    .valorTotal(valor)
                    .natureza(lancamento.getNatureza())
                    .build();
        }).forEach(itens::add);

        BigDecimal total = itens.stream()
                .map(ItemFechamento::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Fechamento calculado - Total de itens: {}, Valor total: {}", itens.size(), total);

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

        if (!lancamentosAbertos.isEmpty()) {
            lancamentoRepository.marcarLancamentosComoProcessados(
                    lancamentosAbertos.stream()
                            .map(Lancamento::getId)
                            .collect(Collectors.toList()),
                    salvo.getId()
            );
            log.info("Marcados {} lançamentos como processados", lancamentosAbertos.size());
        }

        log.info("Fechamento concluído com sucesso: id={}", salvo.getId());

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
