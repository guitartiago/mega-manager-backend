package com.megamanager.cliente.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Cliente {
	
	@Setter
	private Long id;
	
	private String nome;
	private String email;
	private PerfilCliente perfil;

	private Cliente(Long id, String nome, String email, PerfilCliente perfil) {

		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("Nome não pode ser vazio");
		}
		
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Email não pode ser vazio");
		}
		
		if (perfil == null) {
			throw new IllegalArgumentException("Perfil do cliente é obrigatório");
		}

		this.id = id;
		this.nome = nome;
		this.email = email;
		this.perfil = perfil;
	}
	
	public static Cliente reconstruir(Long id, String nome, String email, PerfilCliente perfil) {
        return new Cliente(id, nome, email, perfil);
    }

    public static Cliente criar(String nome, String email, PerfilCliente perfil) {
        return new Cliente(null, nome, email, perfil);
    }

}
