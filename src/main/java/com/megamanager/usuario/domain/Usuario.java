package com.megamanager.usuario.domain;

import java.util.Objects;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Usuario {

    private final Long id;
    private final String username;
    private final String senhaHash;
    private final boolean ativo;
    private final Set<PerfilUsuario> perfis;

    private Usuario(Long id, String username, String senhaHash, boolean ativo, Set<PerfilUsuario> perfis) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username é obrigatório");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("senhaHash é obrigatório");
        }
        if (perfis == null || perfis.isEmpty()) {
            throw new IllegalArgumentException("perfis é obrigatório e não pode estar vazio");
        }

        this.id = id;
        this.username = username;
        this.senhaHash = senhaHash;
        this.ativo = ativo;
        this.perfis = Set.copyOf(perfis);
    }

    public static Usuario criar(String username, String senhaHash, Set<PerfilUsuario> perfis) {
        return new Usuario(null, username, senhaHash, true, perfis);
    }

    public static Usuario reconstruir(Long id, String username, String senhaHash, boolean ativo, Set<PerfilUsuario> perfis) {
        return new Usuario(id, username, senhaHash, ativo, perfis);
    }

    public Usuario inativar() {
        return new Usuario(this.id, this.username, this.senhaHash, false, this.perfis);
    }

    public Usuario ativar() {
        return new Usuario(this.id, this.username, this.senhaHash, true, this.perfis);
    }

    public Usuario alterarPerfis(Set<PerfilUsuario> novosPerfis) {
        return new Usuario(this.id, this.username, this.senhaHash, this.ativo, novosPerfis);
    }

    public Usuario alterarSenha(String novaSenhaHash) {
        return new Usuario(this.id, this.username, novaSenhaHash, this.ativo, this.perfis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
