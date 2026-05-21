package com.megamanager.consumo.adapter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import com.megamanager.auth.application.port.out.TokenProvider;
import com.megamanager.consumo.application.port.in.ListarConsumosPorClienteUseCase;
import com.megamanager.consumo.application.port.in.RegistrarConsumoUseCase;
import com.megamanager.consumo.application.port.in.DetalharContaClienteV2UseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.megamanager.cliente.domain.PerfilCliente;
import com.megamanager.consumo.application.dto.ExtratoContaCliente;
import com.megamanager.consumo.application.dto.ItemExtratoDTO;
import com.megamanager.consumo.application.port.in.DetalharContaClienteUseCase;

@WebMvcTest(ConsumoController.class)
@AutoConfigureMockMvc(addFilters = false)
class DetalharContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private DetalharContaClienteUseCase detalharContaClienteUseCase;

    @MockBean
    private DetalharContaClienteV2UseCase detalharContaClienteV2UseCase;

    @MockBean
    private RegistrarConsumoUseCase registrarConsumoUseCase;

    @MockBean
    private ListarConsumosPorClienteUseCase listarConsumosPorClienteUseCase;

    @Test
    @DisplayName("Deve retornar extrato da conta do cliente com status 200")
    void deveRetornarExtratoContaCliente() throws Exception {
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

        Mockito.when(detalharContaClienteUseCase.detalharConta(clienteId)).thenReturn(extrato);

        mockMvc.perform(get("/consumos/detalhar-conta/{clienteId}", clienteId)
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
