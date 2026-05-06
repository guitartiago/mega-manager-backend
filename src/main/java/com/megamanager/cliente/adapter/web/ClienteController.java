package com.megamanager.cliente.adapter.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.cliente.adapter.web.dto.ClienteRequestDTO;
import com.megamanager.cliente.adapter.web.dto.ClienteResponseDTO;
import com.megamanager.cliente.adapter.web.mapper.ClienteDtoMapper;
import com.megamanager.cliente.application.port.in.AtualizarClienteUseCase;
import com.megamanager.cliente.application.port.in.BuscarClienteUseCase;
import com.megamanager.cliente.application.port.in.CadastrarClienteUseCase;
import com.megamanager.cliente.application.port.in.ExcluirClienteUseCase;
import com.megamanager.cliente.domain.Cliente;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final ExcluirClienteUseCase excluirClienteUseCase;

    @PostMapping
    @Operation(summary = "Criar novo cliente")
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody @Valid ClienteRequestDTO dto) {
        Cliente cliente = ClienteDtoMapper.toDomain(dto);
        Cliente criado = cadastrarClienteUseCase.cadastrar(cliente);
        ClienteResponseDTO response = ClienteDtoMapper.toResponse(criado);
        return ResponseEntity.created(URI.create("/clientes/" + criado.getId())).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar clientes com paginação")
    public ResponseEntity<PaginatedResponse<ClienteResponseDTO>> listarTodos(
            @Parameter(description = "Página (começando em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        List<Cliente> clientes = buscarClienteUseCase.listarTodos();
        
        // Implementar paginação simples
        int start = page * size;
        int end = Math.min(start + size, clientes.size());
        
        List<ClienteResponseDTO> content = clientes.subList(start, end)
                .stream()
                .map(ClienteDtoMapper::toResponse)
                .toList();
        
        PaginatedResponse<ClienteResponseDTO> response = PaginatedResponse.<ClienteResponseDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .total(clientes.size())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return buscarClienteUseCase.buscarPorId(id)
                .map(ClienteDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid ClienteRequestDTO dto) {
        Cliente cliente = ClienteDtoMapper.toDomain(dto);
        return atualizarClienteUseCase.atualizar(id, cliente)
                .map(ClienteDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        excluirClienteUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
