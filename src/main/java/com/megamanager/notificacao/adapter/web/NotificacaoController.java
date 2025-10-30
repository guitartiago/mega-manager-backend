package com.megamanager.notificacao.adapter.web;

import com.megamanager.notificacao.application.port.in.EnviarContaPixEmailUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/notificacao")
public class NotificacaoController {

    private final EnviarContaPixEmailUseCase useCase;

    public NotificacaoController(EnviarContaPixEmailUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/enviar-conta")
    public ResponseEntity<Void> enviarConta(@RequestParam String emailDestino,
                                            @RequestParam String nomeCliente,
                                            @RequestParam BigDecimal valor,
                                            @RequestParam(required = false, defaultValue = "") String descricao,
                                            @RequestParam(required = false, defaultValue = "true") boolean anexar) {
        useCase.execute(new EnviarContaPixEmailUseCase.Command(emailDestino, nomeCliente, valor, descricao, anexar));
        return ResponseEntity.ok().build();
    }
}
