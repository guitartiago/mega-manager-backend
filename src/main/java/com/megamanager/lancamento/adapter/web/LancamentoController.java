package com.megamanager.lancamento.adapter.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.lancamento.adapter.web.dto.LancamentoRequestDTO;
import com.megamanager.lancamento.adapter.web.dto.EstornarLancamentoRequestDTO;
import com.megamanager.lancamento.adapter.web.dto.LancamentoResponseDTO;
import com.megamanager.lancamento.adapter.web.mapper.LancamentoDtoMapper;
import com.megamanager.lancamento.application.port.in.EstornarLancamentoUseCase;
import com.megamanager.lancamento.application.port.in.ListarLancamentosPorClienteUseCase;
import com.megamanager.lancamento.application.port.in.RegistrarLancamentoUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final RegistrarLancamentoUseCase registrarLancamentoUseCase;
    private final ListarLancamentosPorClienteUseCase listarLancamentosPorClienteUseCase;
    private final EstornarLancamentoUseCase estornarLancamentoUseCase;

    @Operation(summary = "Registra um lançamento (débito/crédito) na conta do cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamento registrado com sucesso")
    })
    @PostMapping
    public ResponseEntity<LancamentoResponseDTO> registrar(@RequestBody @Valid LancamentoRequestDTO request,
                                                           Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "sistema";
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");

        var command = LancamentoDtoMapper.toCommand(request, username, isAdmin);
        var salvo = registrarLancamentoUseCase.executar(command);
        return ResponseEntity.ok(LancamentoDtoMapper.toResponse(salvo));
    }

    @Operation(summary = "Lista os lançamentos do cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem retornada com sucesso")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<LancamentoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        var lista = listarLancamentosPorClienteUseCase.listarPorCliente(clienteId);
        var resp = lista.stream().map(LancamentoDtoMapper::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Estorna um lançamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estorno registrado com sucesso")
    })
    @PostMapping("/{lancamentoId}/estornar")
    public ResponseEntity<LancamentoResponseDTO> estornar(@PathVariable Long lancamentoId,
                                                          @RequestBody(required = false) EstornarLancamentoRequestDTO request,
                                                          Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "sistema";
        String motivoFinal = (request != null && request.getMotivo() != null && !request.getMotivo().isBlank())
                ? request.getMotivo()
                : "Estorno";

        var command = new EstornarLancamentoUseCase.EstornarLancamentoCommand(lancamentoId, motivoFinal, username);
        var estorno = estornarLancamentoUseCase.executar(command);
        return ResponseEntity.ok(LancamentoDtoMapper.toResponse(estorno));
    }

    private boolean hasRole(Authentication auth, String role) {
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (role.equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
