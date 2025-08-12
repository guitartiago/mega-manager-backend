package com.megamanager.estoque.adapter.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import com.megamanager.estoque.application.port.out.EntradaEstoqueRepository;
import com.megamanager.estoque.domain.EntradaEstoque;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntradaEstoqueRepositoryAdapter implements EntradaEstoqueRepository {

    private final EntradaEstoqueJpaRepository produtoJpaRepository;

    @Override
    public EntradaEstoque salvar(EntradaEstoque produto) {
        EntradaEstoqueEntity entity = EntradaEstoqueMapper.toEntity(produto);
        EntradaEstoqueEntity salvo = produtoJpaRepository.save(entity);
        return EntradaEstoqueMapper.toDomain(salvo);
    }

	@Override
	public List<EntradaEstoque> listarPorProduto(Long idProduto) {
		// TODO Auto-generated method stub
		return null;
	}
}
