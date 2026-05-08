package com.megamanager.consumo.application.usecase;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.cliente.domain.Cliente;
import com.megamanager.common.exception.ClienteNaoEncontradoException;
import com.megamanager.common.exception.ProdutoNaoEncontradoException;
import com.megamanager.consumo.application.port.in.ListarConsumosPorClienteUseCase;
import com.megamanager.consumo.application.port.in.RegistrarConsumoUseCase;
import com.megamanager.consumo.application.port.out.ConsumoRepository;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de caso de uso para gerenciar consumos.
 * Responsável pela orquestração da lógica de negócio, delegando detalhes
 * de complexidade (como abate de estoque) para serviços especializados.
 */
@RequiredArgsConstructor
public class GerenciarConsumoService implements RegistrarConsumoUseCase, ListarConsumosPorClienteUseCase {

    private static final Logger log = LoggerFactory.getLogger(GerenciarConsumoService.class);

    private final ConsumoRepository consumoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final AbaterEstoqueService abaterEstoqueService;

    @Override
    public Consumo registrar(Consumo novoConsumo) {
        log.info("Iniciando registro de consumo para cliente [{}] - produto [{}] - quantidade [{}]",
                novoConsumo.getClienteId(),
                novoConsumo.getDadosProduto().getProdutoId(),
                novoConsumo.getDadosProduto().getQuantidade());

        Cliente cliente = validarCliente(novoConsumo.getClienteId());
        Produto produto = validarProduto(novoConsumo.getDadosProduto().getProdutoId());

        Consumo consumo = abaterEstoqueService.abaterESalvar(novoConsumo, cliente, produto);

        log.info("Consumo registrado com sucesso para cliente [{}] - produto [{}]",
                novoConsumo.getClienteId(),
                novoConsumo.getDadosProduto().getProdutoId());

        return consumo;
    }

    @Override
    public List<Consumo> listarPorCliente(Long clienteId) {
        log.info("Listando consumos do cliente [{}]", clienteId);
        return consumoRepository.buscarPorCliente(clienteId);
    }

    private Cliente validarCliente(Long clienteId) {
        return clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));
    }

    private Produto validarProduto(Long produtoId) {
        return produtoRepository.buscarPorId(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));
    }
}
