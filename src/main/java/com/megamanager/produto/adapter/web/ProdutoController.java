package com.megamanager.produto.adapter.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.produto.adapter.web.dto.ProdutoRequestDTO;
import com.megamanager.produto.adapter.web.dto.ProdutoResponseDTO;
import com.megamanager.produto.adapter.web.mapper.ProdutoDtoMapper;
import com.megamanager.produto.application.port.in.CadastrarProdutoUseCase;
import com.megamanager.produto.application.port.in.ListarProdutosUseCase;
import com.megamanager.produto.domain.Produto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final CadastrarProdutoUseCase cadastrarProdutoUseCase;
    private final ListarProdutosUseCase listarProdutosUseCase;

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@RequestBody @Valid ProdutoRequestDTO dto) {
        Produto produto = ProdutoDtoMapper.toDomain(dto);
        Produto salvo = cadastrarProdutoUseCase.cadastrar(produto);
        ProdutoResponseDTO response = ProdutoDtoMapper.toResponse(salvo);
        return ResponseEntity.created(URI.create("/produtos/" + response.getId())).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<Produto> clientes = listarProdutosUseCase.listarTodos();
        List<ProdutoResponseDTO> response = clientes.stream()
                .map(ProdutoDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/lote")
    public ResponseEntity<List<ProdutoResponseDTO>> cadastrar(@RequestBody @Valid List<ProdutoRequestDTO> dtos) {
    	
    	List<ProdutoResponseDTO> response = dtos.stream()
    			.map(ProdutoDtoMapper::toDomain)
    			.map(cadastrarProdutoUseCase::cadastrar)
    			.map(ProdutoDtoMapper::toResponse).toList();
    	
        return ResponseEntity.created(URI.create("/produtos")).body(response);
    }
}
