package com.megamanager.adapter.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamanager.application.usecase.GerenciarClienteUseCase;
import com.megamanager.domain.model.Cliente;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	private final GerenciarClienteUseCase gerenciarClienteUseCase;

    public ClienteController(GerenciarClienteUseCase useCase) {
        this.gerenciarClienteUseCase = useCase;
    }

    @PostMapping
    public Cliente cadastrar(@RequestBody Cliente cliente) {
        return gerenciarClienteUseCase.cadastrar(cliente);
    }

    @GetMapping
    public List<Cliente> listarTodos() {
        return gerenciarClienteUseCase.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return gerenciarClienteUseCase.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return gerenciarClienteUseCase.atualizar(id, cliente)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        gerenciarClienteUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
