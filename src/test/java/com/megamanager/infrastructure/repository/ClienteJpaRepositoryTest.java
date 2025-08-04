package com.megamanager.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.megamanager.domain.model.PerfilCliente;
import com.megamanager.persistence.entity.ClienteEntity;

@DataJpaTest
public class ClienteJpaRepositoryTest {

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Test
    void deveSalvarClienteComSucesso() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Tiago");
        cliente.setEmail("tiago@example.com");
        cliente.setPerfil(PerfilCliente.SOCIO);

        ClienteEntity salvo = clienteJpaRepository.save(cliente);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Tiago");
    }

    @Test
    void deveBuscarClientePorId() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Buscar");
        cliente.setEmail("buscar@example.com");
        cliente.setPerfil(PerfilCliente.COMUM);

        ClienteEntity salvo = clienteJpaRepository.save(cliente);

        Optional<ClienteEntity> encontrado = clienteJpaRepository.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Buscar");
    }
}
