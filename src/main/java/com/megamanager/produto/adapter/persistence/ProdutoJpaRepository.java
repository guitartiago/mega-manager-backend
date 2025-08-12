package com.megamanager.produto.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoJpaRepository extends JpaRepository<ProdutoEntity, Long> {
    // Aqui você pode adicionar métodos customizados se necessário no futuro
}
