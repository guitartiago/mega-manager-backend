package com.megamanager.produto.application.port.out;

import com.megamanager.produto.domain.Produto;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {
    Produto salvar(Produto produto);
    List<Produto> listarTodos();
    Optional<Produto> buscarPorId(Long id);
}
