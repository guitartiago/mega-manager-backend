package com.megamanager.fechamento.adapter.web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.megamanager.fechamento.adapter.web.dto.FechamentoResponseDTO;
import com.megamanager.fechamento.adapter.web.dto.FechamentoResumoDTO;
import com.megamanager.fechamento.adapter.web.mapper.FechamentoDtoMapper;
import com.megamanager.fechamento.application.port.in.BuscarFechamentoUseCase;
import com.megamanager.fechamento.application.port.in.FecharContaClienteUseCase;
import com.megamanager.fechamento.application.port.in.ListarFechamentosUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/fechamentos")
@RequiredArgsConstructor
public class FechamentoContaController {

    private final FecharContaClienteUseCase fecharContaUseCase;
    private final ListarFechamentosUseCase listarUseCase;
    private final BuscarFechamentoUseCase buscarUseCase;

    @Operation(summary = "Fecha a conta do cliente, registra histórico e retorna o fechamento")
    @ApiResponse(responseCode = "201", description = "Conta fechada com sucesso")
    @PostMapping("/fechar/{clienteId}")
    public ResponseEntity<FechamentoResponseDTO> fechar(@PathVariable Long clienteId) {
        var fechamento = fecharContaUseCase.fechar(clienteId);
        return ResponseEntity.status(201).body(FechamentoDtoMapper.toResponse(fechamento));
    }

    @Operation(summary = "Lista fechamentos (opcional: filtrar por cliente e período)")
    @GetMapping
    public ResponseEntity<List<FechamentoResumoDTO>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ate) {

        var inicio = (de != null) ? de : LocalDateTime.now().minusDays(30);
        var fim    = (ate != null) ? ate : LocalDateTime.now();

        var lista = listarUseCase.listar(clienteId, inicio, fim)
                .stream().map(FechamentoDtoMapper::toResumo).toList();

        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Detalha um fechamento pelo id (com itens)")
    @GetMapping("/{id}")
    public ResponseEntity<FechamentoResponseDTO> detalhar(@PathVariable Long id) {
        return buscarUseCase.buscarPorId(id)
                .map(f -> ResponseEntity.ok(FechamentoDtoMapper.toResponse(f)))
                .orElse(ResponseEntity.notFound().build());
    }
}
