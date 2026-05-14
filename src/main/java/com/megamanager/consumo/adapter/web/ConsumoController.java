package com.megamanager.consumo.adapter.web;

import com.megamanager.consumo.adapter.web.dto.ConsumoRequestDTO;
import com.megamanager.consumo.adapter.web.dto.ConsumoResponseDTO;
import com.megamanager.consumo.adapter.web.dto.DetalheContaDTO;
import com.megamanager.consumo.adapter.web.mapper.ConsumoDtoMapper;
import com.megamanager.consumo.application.dto.ExtratoContaCliente;
import com.megamanager.consumo.application.port.in.DetalharContaClienteUseCase;
import com.megamanager.consumo.application.port.in.DetalharContaClienteV2UseCase;
import com.megamanager.consumo.application.port.in.ListarConsumosPorClienteUseCase;
import com.megamanager.consumo.application.port.in.RegistrarConsumoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consumos")
@RequiredArgsConstructor
public class ConsumoController {

    private final RegistrarConsumoUseCase registrarConsumoUseCase;
    private final ListarConsumosPorClienteUseCase listarConsumosPorClienteUseCase;
    private final DetalharContaClienteUseCase detalharContaClienteUseCase;
    private final DetalharContaClienteV2UseCase detalharContaClienteV2UseCase;

    
    @Operation(
        summary = "Registra o consumo de um produto por um cliente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consumo registrado com sucesso"),
    })
    @PostMapping
    public ResponseEntity<ConsumoResponseDTO> registrarConsumo(@RequestBody @Valid ConsumoRequestDTO requestDTO) {
        var dominio = ConsumoDtoMapper.toDomain(requestDTO, null, null);
        var consumoRegistrado = registrarConsumoUseCase.registrar(dominio);
        return ResponseEntity.ok(ConsumoDtoMapper.toResponse(consumoRegistrado));
    }
    
    @Operation(
        summary = "Detalhar a conta do cliente",
        description = "Gera um extrato dos consumos não pagos para o cliente informado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extrato gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Cliente não encontrado")
    })
    @GetMapping("/detalhar-conta/{clienteId}")
    public ResponseEntity<ExtratoContaCliente> detalharConta(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        ExtratoContaCliente extrato = detalharContaClienteUseCase.detalharConta(clienteId);
        return ResponseEntity.ok(extrato);
    }

    @Operation(
            summary = "Detalhar a conta do cliente V2",
            description = "Gera um extrato dos consumos não pagos para o cliente informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extrato gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado")
    })
    @GetMapping("/detalhar-conta/v2/{clienteId}")
    public ResponseEntity<DetalheContaDTO> detalharContaV2(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        DetalheContaDTO detalheContaDTO = detalharContaClienteV2UseCase.execute(clienteId);
        return ResponseEntity.ok(detalheContaDTO);
    }
    
    
    @Operation(
        summary = "Lista todos os consumos do Cliente",
        description = "listagem com o histórico de consumo de um cliente, mostrando até os consumos pagos"
    )
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ConsumoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        var consumos = listarConsumosPorClienteUseCase.listarPorCliente(clienteId);
        var responses = consumos.stream()
                .map(ConsumoDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
}
