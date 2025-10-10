package com.megamanager.fechamento.adapter.web.mapper;

import java.util.List;
import com.megamanager.fechamento.adapter.web.dto.FechamentoResponseDTO;
import com.megamanager.fechamento.adapter.web.dto.FechamentoResumoDTO;
import com.megamanager.fechamento.adapter.web.dto.ItemFechamentoDTO;
import com.megamanager.fechamento.domain.FechamentoConta;
import com.megamanager.fechamento.domain.ItemFechamento;

public class FechamentoDtoMapper {

    public static FechamentoResponseDTO toResponse(FechamentoConta f) {
        return FechamentoResponseDTO.builder()
                .id(f.getId())
                .clienteId(f.getClienteId())
                .clienteNome(f.getClienteNome())
                .usuario(f.getUsuarioUsername())
                .dataHora(f.getDataHora())
                .total(f.getTotalPago())
                .itens(toItemDtos(f.getItens()))
                .build();
    }

    public static FechamentoResumoDTO toResumo(FechamentoConta f) {
        return FechamentoResumoDTO.builder()
                .id(f.getId())
                .clienteNome(f.getClienteNome())
                .usuario(f.getUsuarioUsername())
                .dataHora(f.getDataHora())
                .total(f.getTotalPago())
                .build();
    }

    private static List<ItemFechamentoDTO> toItemDtos(List<ItemFechamento> itens) {
        return itens.stream().map(i ->
                ItemFechamentoDTO.builder()
                        .produtoId(i.getProdutoId())
                        .nomeProduto(i.getNomeProduto())
                        .quantidade(i.getQuantidade())
                        .valorUnitario(i.getValorUnitario())
                        .valorTotal(i.getValorTotal())
                        .build()
        ).toList();
    }
}
