package com.megamanager.consumo.adapter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.dto.ExtratoContaCliente;
import com.megamanager.consumo.application.dto.ItemExtratoDTO;
import com.megamanager.consumo.application.port.in.DetalharContaClienteUseCase;

@WebMvcTest(ConsumoController.class)
class FecharContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DetalharContaClienteUseCase fecharContaClienteUseCase;


    @Test
    @DisplayName("Deve retornar extrato da conta do cliente com status 200")
    void deveRetornarExtratoContaCliente() throws Exception {
        // Arrange
        Long clienteId = 1L;
        ExtratoContaCliente extrato = ExtratoContaCliente.builder()
                .clienteId(clienteId)
                .nomeCliente("Tiago")
                .perfil(PerfilCliente.COMUM)
                .total(new BigDecimal("12.00"))
                .itens(List.of(ItemExtratoDTO.builder()
                        .nomeProduto("Budweiser 269ml")
                        .quantidade(2)
                        .valorUnitario(new BigDecimal("6.00"))
                        .valorTotal(new BigDecimal("12.00"))
                        .build()))
                .build();

        Mockito.when(fecharContaClienteUseCase.detalharConta(clienteId)).thenReturn(extrato);

        // Act & Assert
        mockMvc.perform(get("/consumos/fechar-conta/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.nomeCliente").value("Tiago"))
                .andExpect(jsonPath("$.perfil").value("COMUM"))
                .andExpect(jsonPath("$.total").value(12.00))
                .andExpect(jsonPath("$.itens").isArray())
                .andExpect(jsonPath("$.itens[0].nomeProduto").value("Budweiser 269ml"))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2))
                .andExpect(jsonPath("$.itens[0].valorUnitario").value(6.00))
                .andExpect(jsonPath("$.itens[0].valorTotal").value(12.00));
    }
}
