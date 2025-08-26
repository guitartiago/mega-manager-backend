package com.megamanager.estoque.adapter.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntradaEstoqueJpaRepository extends JpaRepository<EntradaEstoqueEntity, Long> {

    // Recupera todas as entradas de estoque para um produto específico
    List<EntradaEstoqueEntity> findByProdutoId(Long produtoId);

    // (Opcional) Recupera ordenado por data de compra
    List<EntradaEstoqueEntity> findByProdutoIdOrderByDataCompraAsc(Long produtoId);
}
