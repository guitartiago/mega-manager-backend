package com.megamanager.cliente.adapter.web.dto;

import com.megamanager.cliente.domain.PerfilCliente;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClienteResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private PerfilCliente perfil;
}
