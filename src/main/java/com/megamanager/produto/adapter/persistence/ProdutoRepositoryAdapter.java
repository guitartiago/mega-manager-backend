package com.megamanager.produto.adapter.persistence;

import com.megamanager.produto.application.port.out.ProdutoRepository;
import com.megamanager.produto.domain.Produto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProdutoRepositoryAdapter implements ProdutoRepository {

    private final ProdutoJpaRepository produtoJpaRepository;

    @Override
    public Produto salvar(Produto produto) {
        ProdutoEntity entity = ProdutoMapper.toEntity(produto);
        ProdutoEntity salvo = produtoJpaRepository.save(entity);
        return ProdutoMapper.toDomain(salvo);
    }

    @Override
    public List<Produto> listarTodos() {
        return produtoJpaRepository.findAll()
                .stream()
                .map(ProdutoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return produtoJpaRepository.findById(id)
                .map(ProdutoMapper::toDomain);
    }
}
