package com.megamanager.infrastructure.controller;

import com.megamanager.application.usecase.CadastrarClienteUseCase;
import com.megamanager.domain.model.Cliente;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;

    public ClienteController(CadastrarClienteUseCase useCase) {
        this.cadastrarClienteUseCase = useCase;
    }

    @PostMapping
    public Cliente cadastrar(@RequestBody Cliente cliente) {
        return cadastrarClienteUseCase.executar(cliente);
    }
}
