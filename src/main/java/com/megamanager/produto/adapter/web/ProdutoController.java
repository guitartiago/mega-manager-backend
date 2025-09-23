package com.megamanager.produto.adapter.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.produto.adapter.web.dto.ProdutoRequestDTO;
import com.megamanager.produto.adapter.web.dto.ProdutoResponseDTO;
import com.megamanager.produto.adapter.web.mapper.ProdutoDtoMapper;
import com.megamanager.produto.application.port.in.AtualizarProdutoUseCase;
import com.megamanager.produto.application.port.in.BuscarProdutoUseCase;
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
    private final BuscarProdutoUseCase buscarProdutoUseCase;
    private final AtualizarProdutoUseCase atualizarProdutoUseCase;

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
    
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return buscarProdutoUseCase.buscarPorId(id)
                .map(ProdutoDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid ProdutoRequestDTO dto) {
        Produto produto = ProdutoDtoMapper.toDomain(dto);
        return atualizarProdutoUseCase.atualizar(id, produto)
                .map(ProdutoDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
