package com.megamanager.estoque.adapter.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.estoque.domain.EntradaEstoque;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntradaEstoqueRepositoryAdapter implements EntradaEstoqueRepository {

    private final EntradaEstoqueJpaRepository estoqueJpaRepository;

    @Override
    public EntradaEstoque salvar(EntradaEstoque produto) {
        EntradaEstoqueEntity entity = EntradaEstoqueMapper.toEntity(produto);
        EntradaEstoqueEntity salvo = estoqueJpaRepository.save(entity);
        return EntradaEstoqueMapper.toDomain(salvo);
    }

	@Override
	public List<EntradaEstoque> buscarPorProdutoId(Long produtoId) {
		return estoqueJpaRepository.findByProdutoId(produtoId).stream().map(EntradaEstoqueMapper::toDomain).toList();
	}
}
