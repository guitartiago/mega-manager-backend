package com.megamanager.application.port.out;

import com.megamanager.domain.model.Cliente;
import java.util.Optional;
import java.util.List;

public interface ClienteRepository {
    
	Cliente salvar(Cliente cliente);
    
	Optional<Cliente> buscarPorId(Long id);
    
	List<Cliente> listarTodos();
		
	void excluir(Long id);
}