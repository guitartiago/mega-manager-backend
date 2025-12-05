package com.megamanager.consumo.application.usecase;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.port.in.ListarConsumosPorClienteUseCase;
import com.megamanager.consumo.application.port.in.RegistrarConsumoUseCase;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.consumo.domain.DadosProduto;
import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.estoque.domain.EntradaEstoque;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class GerenciarConsumoService implements RegistrarConsumoUseCase, ListarConsumosPorClienteUseCase {

    private static final Logger log = LoggerFactory.getLogger(GerenciarConsumoService.class);

    private final ConsumoRepository consumoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EntradaEstoqueRepository entradaEstoqueRepository;

    @Override
    @Transactional
    public Consumo registrar(Consumo consumo) {
        log.info("➡️ Iniciando registro de consumo para cliente {} - produto {} - quantidade {}",
                consumo.getClienteId()
                , consumo.getDadosProduto().getProdutoId()
                , consumo.getDadosProduto().getQuantidade());

        Cliente cliente = clienteRepository.buscarPorId(consumo.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Produto produto = produtoRepository.buscarPorId(consumo.getDadosProduto().getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        boolean isSocio = cliente.getPerfil() == PerfilCliente.SOCIO;

        List<EntradaEstoque> entradasEstoque = entradaEstoqueRepository
                .buscarPorProdutoId(consumo.getDadosProduto().getProdutoId())
                .stream()
                .filter(EntradaEstoque::possuiSaldoDisponivel)
                .sorted(Comparator.comparing(EntradaEstoque::getDataCompra))
                .toList();

        int restante = consumo.getDadosProduto().getQuantidade();
        Consumo ultimoConsumo = null;

        BigDecimal valorProdutoParaSocio = BigDecimal.ZERO;

        for (EntradaEstoque entradaEstoque : entradasEstoque) {
            if (restante <= 0) break;

            int podeAbater = Math.min(restante, entradaEstoque.getSaldo());

            log.debug("Abatendo {} unidade(s) da entrada #{} (saldo atual: {})",
                    podeAbater, entradaEstoque.getId(), entradaEstoque.getSaldo());

            entradaEstoque.abaterSaldo(podeAbater);
            entradaEstoqueRepository.salvar(entradaEstoque);

            valorProdutoParaSocio = entradaEstoque.getPrecoCustoUnitario();

            BigDecimal valorUnitario = isSocio
                    ? valorProdutoParaSocio
                    : produto.getPrecoVenda();

            Consumo parcial = Consumo.criar(
                    consumo.getClienteId(),
                    new DadosProduto(
                    		consumo.getDadosProduto().getProdutoId(), 
                    		podeAbater, 
                    		valorUnitario
                    	),
                    consumo.getDataHora(),
                    entradaEstoque.getId()
            );

            consumoRepository.salvar(parcial);
            restante -= podeAbater;
            ultimoConsumo = parcial;

        }

        if (restante > 0) {

            BigDecimal valorUnitario = isSocio
                    ? valorProdutoParaSocio
                    : produto.getPrecoVenda();

            log.warn("⚠️ Estoque insuficiente para produto {}! Registrando {} unidade(s) SEM abate de entrada com o valor {}.",
                    produto.getId(), restante, valorUnitario);

            Consumo fallback = Consumo.criar(
                    consumo.getClienteId(),
                    new DadosProduto(
                    		consumo.getDadosProduto().getProdutoId(),
                    		restante,
                    		valorUnitario
                    	),
                    consumo.getDataHora(),
                    null
            );

            consumoRepository.salvar(fallback);
            ultimoConsumo = fallback;
        }

        log.info("✅ Registro de consumo finalizado para cliente {} - produto {}",
                consumo.getClienteId(), consumo.getDadosProduto().getProdutoId());

        return ultimoConsumo;
    }

    @Override
    public List<Consumo> listarPorCliente(Long clienteId) {
        log.info("📋 Listando consumos do cliente {}", clienteId);
        return consumoRepository.buscarPorCliente(clienteId);
    }
    
}
