package com.megamanager.produto.adapter.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProdutoRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Preço de venda é obrigatório")
    private BigDecimal precoVenda;
    
    private boolean ativo;
}
