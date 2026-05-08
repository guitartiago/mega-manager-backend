package com.megamanager.lancamento.adapter.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class LancamentoRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    /** DEBITO ou CREDITO */
    @NotNull(message = "Natureza é obrigatória")
    private String natureza;

    /** PAGAMENTO, SERVICO, DESCONTO, CORRECAO, COBRANCA_ADICIONAL */
    @NotNull(message = "Categoria é obrigatória")
    private String categoria;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    private String motivo;

    /** Opcional (default: now) */
    private LocalDateTime dataHora;
}
