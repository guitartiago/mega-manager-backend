package com.megamanager.produto.adapter.web.mapper;

import com.megamanager.produto.adapter.web.dto.ProdutoRequestDTO;
import com.megamanager.produto.adapter.web.dto.ProdutoResponseDTO;
import com.megamanager.produto.domain.Produto;

public class ProdutoDtoMapper {

    public static Produto toDomain(ProdutoRequestDTO dto) {
        return Produto.criar(
        		dto.getNome(),
        		dto.getPrecoVenda()
        	);
    }

    public static ProdutoResponseDTO toResponse(Produto produto) {
        return new ProdutoResponseDTO(
            produto.getId(),
            produto.getNome(),
            produto.getPrecoVenda(),
            produto.isAtivo()
        );
    }
}
