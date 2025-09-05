package com.megamanager.auth.adapter.persistence;

import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter @Setter
public class UsuarioEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 80)
  private String username;

  @Column(name = "senha_hash", nullable = false, length = 100)
  private String senhaHash;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "usuario_perfil", joinColumns = @JoinColumn(name = "usuario_id"))
  @Column(name = "perfil", length = 40)
  private Set<String> perfis;

  @Column(nullable = false)
  private boolean ativo = true;
}
