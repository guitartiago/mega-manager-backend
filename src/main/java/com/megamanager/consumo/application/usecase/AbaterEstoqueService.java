package com.megamanager.consumo.application.usecase;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.megamanager.cliente.domain.Cliente;
import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.consumo.domain.DadosProduto;
import com.megamanager.estoque.domain.EntradaEstoque;
import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de abate de estoque ao registrar consumos.
 * Isola a complexidade de cálculo FIFO em um serviço separado.
 */
@RequiredArgsConstructor
public class AbaterEstoqueService {

    private static final Logger log = LoggerFactory.getLogger(AbaterEstoqueService.class);

    private final EntradaEstoqueRepository entradaEstoqueRepository;
    private final ConsumoRepository consumoRepository;

    public Consumo abaterESalvar(Consumo novoConsumo, Cliente cliente, Produto produto) {
        boolean isSocio = cliente.getPerfil() == PerfilCliente.SOCIO;

        List<EntradaEstoque> entradasEstoque = entradaEstoqueRepository
                .buscarPorProdutoId(novoConsumo.getDadosProduto().getProdutoId())
                .stream()
                .filter(EntradaEstoque::possuiSaldoDisponivel)
                .sorted(Comparator.comparing(EntradaEstoque::getDataCompra))
                .toList();

        int restante = novoConsumo.getDadosProduto().getQuantidade();
        Consumo ultimoConsumo = null;

        for (EntradaEstoque entrada : entradasEstoque) {
            if (restante <= 0) break;

            int podeAbater = Math.min(restante, entrada.getSaldo());

            log.debug("Abatendo [{}] unidade(s) da entrada [{}] (saldo atual: [{}])",
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
            log.warn("Estoque insuficiente para produto [{}]. Registrando [{}] unidade(s) SEM abate de entrada.",
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

        return ultimoConsumo;
    }
}
