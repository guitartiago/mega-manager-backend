package com.megamanager.produto.application.port.in;

import com.megamanager.produto.domain.Produto;
import java.util.List;

public interface ListarProdutosUseCase {
    List<Produto> listarTodos();
}
