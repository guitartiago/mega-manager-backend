package com.megamanager.consumo.adapter.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.consumo.adapter.web.dto.ConsumoRequestDTO;
import com.megamanager.consumo.adapter.web.dto.ConsumoResponseDTO;
import com.megamanager.consumo.adapter.web.mapper.ConsumoDtoMapper;
import com.megamanager.consumo.application.port.in.ListarConsumosPorClienteUseCase;
import com.megamanager.consumo.application.port.in.RegistrarConsumoUseCase;
import com.megamanager.produto.application.port.out.ProdutoRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/consumos")
@RequiredArgsConstructor
public class ConsumoController {

    private final RegistrarConsumoUseCase registrarConsumoUseCase;
    private final ListarConsumosPorClienteUseCase listarConsumosUseCase;
    private final ProdutoRepository produtoRepository;

    @PostMapping
    public ResponseEntity<ConsumoResponseDTO> registrarConsumo(@RequestBody @Valid ConsumoRequestDTO requestDTO) {
        var produto = produtoRepository.buscarPorId(requestDTO.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        // Mapper cuida de calcular dataHora e setar entradaEstoqueId = null (temporariamente)
        var dominio = ConsumoDtoMapper.toDomain(requestDTO, produto.getPrecoVenda(), null);

        var consumoRegistrado = registrarConsumoUseCase.registrar(dominio);

        return ResponseEntity.ok(ConsumoDtoMapper.toResponse(consumoRegistrado));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ConsumoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        var consumos = listarConsumosUseCase.listarPorCliente(clienteId);
        var responses = consumos.stream()
                .map(ConsumoDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
