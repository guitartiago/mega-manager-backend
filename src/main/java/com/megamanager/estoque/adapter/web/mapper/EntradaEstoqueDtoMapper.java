package com.megamanager.estoque.adapter.web.mapper;

import java.time.LocalDateTime;

import com.megamanager.estoque.adapter.web.dto.EntradaEstoqueRequestDTO;
import com.megamanager.estoque.adapter.web.dto.EntradaEstoqueResponseDTO;
import com.megamanager.estoque.domain.EntradaEstoque;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EntradaEstoqueDtoMapper {

    public static EntradaEstoque toDomain(EntradaEstoqueRequestDTO dto) {
        return EntradaEstoque.criar(
                dto.getProdutoId(),
                dto.getQuantidade(),
                dto.getPrecoCustoUnitario(),
                LocalDateTime.now() // Data da compra registrada no momento
        );
    }

    public static EntradaEstoqueResponseDTO toResponse(EntradaEstoque entrada) {
        return new EntradaEstoqueResponseDTO(
                entrada.getId(),
                entrada.getProdutoId(),
                entrada.getQuantidade(),
                entrada.getPrecoCustoUnitario(),
                entrada.getDataCompra(),
                entrada.getSaldo()
        );
    }
}
