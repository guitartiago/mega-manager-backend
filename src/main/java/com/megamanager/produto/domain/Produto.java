package com.megamanager.produto.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Produto {
    private Long id;
    private String nome;         
    private BigDecimal precoVenda;  
    private boolean ativo;       
}
