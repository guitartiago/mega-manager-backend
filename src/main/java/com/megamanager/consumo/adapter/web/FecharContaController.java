package com.megamanager.consumo.adapter.web;

import com.megamanager.consumo.application.dto.ExtratoContaCliente;
import com.megamanager.consumo.application.port.in.FecharContaClienteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consumos")
public class FecharContaController {

    private final FecharContaClienteUseCase fecharContaClienteUseCase;

    public FecharContaController(FecharContaClienteUseCase fecharContaClienteUseCase) {
        this.fecharContaClienteUseCase = fecharContaClienteUseCase;
    }

    @Operation(
        summary = "Fecha a conta do cliente",
        description = "Gera um extrato dos consumos não pagos para o cliente informado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extrato gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Cliente não encontrado")
    })
    @GetMapping("/fechar-conta/{clienteId}")
    public ResponseEntity<ExtratoContaCliente> fecharConta(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        ExtratoContaCliente extrato = fecharContaClienteUseCase.fecharConta(clienteId);
        return ResponseEntity.ok(extrato);
    }
}
