package com.megamanager.fechamento.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FechamentoConta {
    private final Long id;

    private final Long clienteId;
    private final String clienteNome;

    private final String usuarioUsername; // quem fechou
    private final LocalDateTime dataHora;

    private final BigDecimal totalPago;

    private final List<ItemFechamento> itens;
}
