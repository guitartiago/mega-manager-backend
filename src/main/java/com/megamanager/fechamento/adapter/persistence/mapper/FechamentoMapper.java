package com.megamanager.fechamento.adapter.persistence.mapper;

import java.util.List;

import com.megamanager.fechamento.adapter.persistence.entity.FechamentoContaEntity;
import com.megamanager.fechamento.adapter.persistence.entity.ItemFechamentoEntity;
import com.megamanager.fechamento.domain.FechamentoConta;
import com.megamanager.fechamento.domain.ItemFechamento;

public class FechamentoMapper {

    public static FechamentoContaEntity toEntity(FechamentoConta d) {
        FechamentoContaEntity e = new FechamentoContaEntity();
        e.setId(d.getId());
        e.setClienteId(d.getClienteId());
        e.setClienteNome(d.getClienteNome());
        e.setUsuarioUsername(d.getUsuarioUsername());
        e.setDataHora(d.getDataHora());
        e.setTotalPago(d.getTotalPago());

        List<ItemFechamentoEntity> itens = d.getItens().stream().map(item -> {
            ItemFechamentoEntity ie = new ItemFechamentoEntity();
            ie.setProdutoId(item.getProdutoId());
            ie.setNomeProduto(item.getNomeProduto());
            ie.setQuantidade(item.getQuantidade());
            ie.setValorUnitario(item.getValorUnitario());
            ie.setValorTotal(item.getValorTotal());
            ie.setFechamento(e);
            return ie;
        }).toList();

        e.setItens(itens);
        return e;
    }

    public static FechamentoConta toDomain(FechamentoContaEntity e) {
        return FechamentoConta.builder()
                .id(e.getId())
                .clienteId(e.getClienteId())
                .clienteNome(e.getClienteNome())
                .usuarioUsername(e.getUsuarioUsername())
                .dataHora(e.getDataHora())
                .totalPago(e.getTotalPago())
                .itens(e.getItens().stream().map(FechamentoMapper::toDomainItem).toList())
                .build();
    }

    private static ItemFechamento toDomainItem(ItemFechamentoEntity ie) {
        return ItemFechamento.builder()
                .produtoId(ie.getProdutoId())
                .nomeProduto(ie.getNomeProduto())
                .quantidade(ie.getQuantidade())
                .valorUnitario(ie.getValorUnitario())
                .valorTotal(ie.getValorTotal())
                .build();
    }
}
