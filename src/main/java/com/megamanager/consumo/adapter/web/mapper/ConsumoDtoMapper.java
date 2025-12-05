package com.megamanager.consumo.adapter.web.mapper;

import java.time.LocalDateTime;

import com.megamanager.consumo.adapter.web.dto.ConsumoRequestDTO;
import com.megamanager.consumo.adapter.web.dto.ConsumoResponseDTO;
import com.megamanager.consumo.domain.Consumo;
import com.megamanager.consumo.domain.DadosProduto;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ConsumoDtoMapper {

    public static Consumo toDomain(ConsumoRequestDTO dto) {
        return toDomain(dto, null, null);
    }

    public static Consumo toDomain(ConsumoRequestDTO dto,
                                   java.math.BigDecimal valorUnitario,
                                   Long entradaEstoqueId) {
        return Consumo.criar(
                dto.getClienteId(),
                new DadosProduto(dto.getProdutoId(), dto.getQuantidade(), valorUnitario),
                LocalDateTime.now(),
                entradaEstoqueId
        );
    }

    public static ConsumoResponseDTO toResponse(Consumo consumo) {
        return ConsumoResponseDTO.builder()
                .id(consumo.getId())
                .clienteId(consumo.getClienteId())
                .produtoId(consumo.getDadosProduto().getProdutoId())
                .quantidade(consumo.getDadosProduto().getQuantidade())
                .valorUnitario(consumo.getDadosProduto().getValorUnitario())
                .valorTotal(consumo.getDadosProduto().getValorTotal())
                .dataHora(consumo.getDataHora())
                .pago(consumo.isPago())
                .entradaEstoqueId(consumo.getEntradaEstoqueId())
                .build();
    }
}
