package com.megamanager.consumo.application.dto;

import com.megamanager.cliente.domain.PerfilCliente;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ExtratoContaCliente {
    private Long clienteId;
    private String nomeCliente;
    private PerfilCliente perfil;
    private List<ItemExtratoDTO> itens;
    private BigDecimal total;
}
