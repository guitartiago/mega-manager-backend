package com.megamanager.cliente.adapter.web;

import com.megamanager.cliente.adapter.web.dto.ClienteRequestDTO;
import com.megamanager.cliente.adapter.web.dto.ClienteResponseDTO;
import com.megamanager.cliente.adapter.web.mapper.ClienteDtoMapper;
import com.megamanager.cliente.application.port.in.*;
import com.megamanager.cliente.domain.Cliente;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final ExcluirClienteUseCase excluirClienteUseCase;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody @Valid ClienteRequestDTO dto) {
        Cliente cliente = ClienteDtoMapper.toDomain(dto);
        Cliente criado = cadastrarClienteUseCase.cadastrar(cliente);
        ClienteResponseDTO response = ClienteDtoMapper.toResponse(criado);
        return ResponseEntity.created(URI.create("/clientes/" + criado.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        List<Cliente> clientes = buscarClienteUseCase.listarTodos();
        List<ClienteResponseDTO> response = clientes.stream()
                .map(ClienteDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return buscarClienteUseCase.buscarPorId(id)
                .map(ClienteDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid ClienteRequestDTO dto) {
        Cliente cliente = ClienteDtoMapper.toDomain(dto);
        return atualizarClienteUseCase.atualizar(id, cliente)
                .map(ClienteDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        excluirClienteUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
