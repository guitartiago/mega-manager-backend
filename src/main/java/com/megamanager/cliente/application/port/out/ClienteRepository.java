package com.megamanager.cliente.application.port.out;

import java.util.List;
import java.util.Optional;

import com.megamanager.cliente.domain.Cliente;

public interface ClienteRepository {
    
	Cliente salvar(Cliente cliente);
    
	Optional<Cliente> buscarPorId(Long id);
    
	List<Cliente> listarTodos();
		
	void excluir(Long id);
}