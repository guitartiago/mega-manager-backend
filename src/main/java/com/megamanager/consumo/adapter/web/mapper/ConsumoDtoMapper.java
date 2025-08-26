package com.megamanager.consumo.adapter.web.mapper;

import com.megamanager.consumo.adapter.web.dto.ConsumoRequestDTO;
import com.megamanager.consumo.adapter.web.dto.ConsumoResponseDTO;
import com.megamanager.consumo.domain.Consumo;

import java.time.LocalDateTime;

public class ConsumoDtoMapper {

    public static Consumo toDomain(ConsumoRequestDTO dto, 
                                   java.math.BigDecimal valorUnitario,
                                   Long entradaEstoqueId) {
        return Consumo.criar(
                dto.getClienteId(),
                dto.getProdutoId(),
                dto.getQuantidade(),
                valorUnitario,
                LocalDateTime.now(),
                entradaEstoqueId
        );
    }

    public static ConsumoResponseDTO toResponse(Consumo consumo) {
        return ConsumoResponseDTO.builder()
                .id(consumo.getId())
                .clienteId(consumo.getClienteId())
                .produtoId(consumo.getProdutoId())
                .quantidade(consumo.getQuantidade())
                .valorUnitario(consumo.getValorUnitario())
                .valorTotal(consumo.getValorTotal())
                .dataHora(consumo.getDataHora())
                .pago(consumo.isPago())
                .entradaEstoqueId(consumo.getEntradaEstoqueId())
                .build();
    }
}
