package com.megamanager.domain.model;

import java.io.Serializable;
import java.util.List;

import com.megamanager.cliente.domain.Cliente;

import lombok.Data;

@Data
public class Banda implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4846954068212853654L;
	
	private Long id;
	private String nome;
	private List<Cliente> integrantes;
		

}
