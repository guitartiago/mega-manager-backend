package com.megamanager.estoque.adapter.web;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.estoque.adapter.web.dto.EntradaEstoqueRequestDTO;
import com.megamanager.estoque.adapter.web.dto.EntradaEstoqueResponseDTO;
import com.megamanager.estoque.adapter.web.mapper.EntradaEstoqueDtoMapper;
import com.megamanager.estoque.application.port.in.ListarEntradasPorProdutoUseCase;
import com.megamanager.estoque.application.port.in.RegistrarEntradaUseCase;
import com.megamanager.estoque.domain.EntradaEstoque;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EntradaEstoqueController {

    private final ListarEntradasPorProdutoUseCase listarEntradasPorProdutoUseCase;
    private final RegistrarEntradaUseCase registrarEntradaUseCase;

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<EntradaEstoqueResponseDTO>> listarPorProduto(@PathVariable Long produtoId) {
        var entradas = listarEntradasPorProdutoUseCase.listarPorProdutoId(produtoId);
        var response = entradas.stream()
                .map(EntradaEstoqueDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EntradaEstoqueResponseDTO> registrar(@RequestBody @Valid EntradaEstoqueRequestDTO dto) {
        EntradaEstoque entrada = EntradaEstoqueDtoMapper.toDomain(dto);
        EntradaEstoque registrada = registrarEntradaUseCase.resgistrar(entrada);
        EntradaEstoqueResponseDTO response = EntradaEstoqueDtoMapper.toResponse(registrada);
        return ResponseEntity.created(URI.create("/estoque/" + response.getId())).body(response);
    }
}
