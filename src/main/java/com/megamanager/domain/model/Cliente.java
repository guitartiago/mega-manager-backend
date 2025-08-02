package com.megamanager.domain.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Cliente implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3845610912879565751L;
	
	private Long id;
	private String nome;
	private String email;
	private PerfilCliente perfil;
	

}
