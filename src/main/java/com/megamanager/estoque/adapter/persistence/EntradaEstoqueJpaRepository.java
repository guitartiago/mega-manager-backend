package com.megamanager.estoque.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntradaEstoqueJpaRepository extends JpaRepository<EntradaEstoqueEntity, Long> {
    // Aqui você pode adicionar métodos customizados se necessário no futuro
}
