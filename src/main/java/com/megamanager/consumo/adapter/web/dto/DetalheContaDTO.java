package com.megamanager.consumo.adapter.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DetalheContaDTO {

    private Long clienteId;
    private String nomeCliente;
    private String perfil;

    private List<ItemConsumoDTO> itens;
    private BigDecimal totalConsumos;

    private List<LancamentoContaDTO> lancamentos;
    private BigDecimal totalLancamentos;

    private BigDecimal total;

}