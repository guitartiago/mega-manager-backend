package com.megamanager;
public class GeradorDeSenha {
	public static void main(String[] args) {
		System.out.println(
				  new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
				    .encode("teste123")
				);
	}
}