package com.megamanager.consumo.application.usecase;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@RequiredArgsConstructor
public class GerenciarConsumoService implements RegistrarConsumoUseCase, ListarConsumosPorClienteUseCase {

    private static final Logger log = LoggerFactory.getLogger(GerenciarConsumoService.class);

    private final ConsumoRepository consumoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EntradaEstoqueRepository entradaEstoqueRepository;

    @Override
    public Consumo registrar(Consumo novoConsumo) {
        log.info("➡️ Iniciando registro de novoConsumo para cliente {} - produto {} - quantidade {}",
                novoConsumo.getClienteId(), novoConsumo.getDadosProduto().getProdutoId(), novoConsumo.getDadosProduto().getQuantidade());

        Cliente cliente = clienteRepository.buscarPorId(novoConsumo.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Produto produto = produtoRepository.buscarPorId(novoConsumo.getDadosProduto().getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        boolean isSocio = cliente.getPerfil() == PerfilCliente.SOCIO;

        List<EntradaEstoque> entradasEstoque = entradaEstoqueRepository.buscarPorProdutoId(novoConsumo.getDadosProduto().getProdutoId()).stream()
                .filter(EntradaEstoque::possuiSaldoDisponivel)
                .sorted(Comparator.comparing(EntradaEstoque::getDataCompra))
                .toList();

        int restante = novoConsumo.getDadosProduto().getQuantidade();
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
                    novoConsumo.getClienteId(),
                    new DadosProduto(
                    		novoConsumo.getDadosProduto().getProdutoId(),
                    		podeAbater, 
                    		valorUnitario
                    	),
                    novoConsumo.getDataHora(),
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
                    novoConsumo.getClienteId(),
                    new DadosProduto(
                    		novoConsumo.getDadosProduto().getProdutoId(),
                    		restante,
                    		valorUnitario
                    	),
                    novoConsumo.getDataHora(),
                    null
            );

            consumoRepository.salvar(fallback);
            ultimoConsumo = fallback;
        }

        log.info("✅ Registro de novoConsumo finalizado para cliente {} - produto {}",
                novoConsumo.getClienteId(), novoConsumo.getDadosProduto().getProdutoId());

        return ultimoConsumo;
    }

    @Override
    public List<Consumo> listarPorCliente(Long clienteId) {
        log.info("📋 Listando consumos do cliente {}", clienteId);
        return consumoRepository.buscarPorCliente(clienteId);
    }
    
}
