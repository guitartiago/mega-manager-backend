package com.megamanager.cliente.adapter.web.dto;

import com.megamanager.cliente.domain.PerfilCliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;
    
    @NotBlank(message = "Celular é obrigatório")
    @Pattern(regexp = "^\\+?\\d{10,13}$", message = "Celular deve conter apenas números (10-13 dígitos)")
    private String celular;

    @NotNull(message = "Perfil é obrigatório")
    private PerfilCliente perfil;
}
