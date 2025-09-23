package com.megamanager.cliente.adapter.persistence;

import com.megamanager.cliente.domain.PerfilCliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "clientes")
@Data
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String nome;
    
    @Email
    private String email;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private PerfilCliente perfil;
    
    @Column(length = 20, nullable = false)
    @NotBlank(message = "celular é obrigatório")
    @Pattern(regexp = "^\\+?\\d{10,13}$", message = "celular inválido")
    private String celular;
}
