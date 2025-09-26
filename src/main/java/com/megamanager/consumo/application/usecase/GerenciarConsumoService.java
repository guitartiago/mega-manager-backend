package com.megamanager.consumo.application.usecase;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.port.in.ListarConsumosNaoPagoPorClienteUseCase;
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

@RequiredArgsConstructor
public class GerenciarConsumoService implements RegistrarConsumoUseCase, ListarConsumosPorClienteUseCase {

    private static final Logger log = LoggerFactory.getLogger(GerenciarConsumoService.class);

    private final ConsumoRepository consumoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EntradaEstoqueRepository entradaEstoqueRepository;

    @Override
    public Consumo registrar(Consumo consumo) {
        log.info("➡️ Iniciando registro de consumo para cliente {} - produto {} - quantidade {}",
                consumo.getClienteId(), consumo.getDadosProduto().getProdutoId(), consumo.getDadosProduto().getQuantidade());

        Cliente cliente = clienteRepository.buscarPorId(consumo.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Produto produto = produtoRepository.buscarPorId(consumo.getDadosProduto().getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        boolean isSocio = cliente.getPerfil() == PerfilCliente.SOCIO;

        List<EntradaEstoque> entradasEstoque = entradaEstoqueRepository.buscarPorProdutoId(consumo.getDadosProduto().getProdutoId()).stream()
                .filter(EntradaEstoque::possuiSaldoDisponivel)
                .sorted(Comparator.comparing(EntradaEstoque::getDataCompra))
                .toList();

        int restante = consumo.getDadosProduto().getQuantidade();
        Consumo ultimoConsumo = null;

        for (EntradaEstoque entrada : entradasEstoque) {
            if (restante <= 0) break;

            int podeAbater = Math.min(restante, entrada.getSaldo());

            log.debug("Abatendo {} unidade(s) da entrada #{} (saldo atual: {})",
                    podeAbater, entrada.getId(), entrada.getSaldo());

            entrada.abaterSaldo(podeAbater);
            entradaEstoqueRepository.salvar(entrada);

            BigDecimal valorUnitario = isSocio
                    ? entrada.getPrecoCustoUnitario()
                    : produto.getPrecoVenda();

            Consumo parcial = Consumo.criar(
                    consumo.getClienteId(),
                    new DadosProduto(
                    		consumo.getDadosProduto().getProdutoId(), 
                    		podeAbater, 
                    		valorUnitario
                    	),
                    consumo.getDataHora(),
                    entrada.getId()
            );

            consumoRepository.salvar(parcial);
            restante -= podeAbater;
            ultimoConsumo = parcial;
        }

        if (restante > 0) {
            log.warn("⚠️ Estoque insuficiente para produto {}! Registrando {} unidade(s) SEM abate de entrada.",
                    produto.getId(), restante);

            BigDecimal valorUnitario = produto.getPrecoVenda();

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
