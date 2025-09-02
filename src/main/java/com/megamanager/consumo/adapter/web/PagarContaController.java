package com.megamanager.consumo.adapter.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.consumo.application.port.in.PagarContaClienteUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/consumos")
public class PagarContaController {

    private final PagarContaClienteUseCase pagarContaClienteUseCase;

    public PagarContaController(PagarContaClienteUseCase pagarContaClienteUseCase) {
        this.pagarContaClienteUseCase = pagarContaClienteUseCase;
    }

    @Operation(summary = "Marca consumos do cliente como pagos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conta paga com sucesso"),
        @ApiResponse(responseCode = "400", description = "Cliente não encontrado")
    })
    @PostMapping("/pagar-conta/{clienteId}")
    public ResponseEntity<Void> pagarConta(@PathVariable Long clienteId) {
        pagarContaClienteUseCase.pagarConta(clienteId);
        return ResponseEntity.noContent().build();
    }
}
