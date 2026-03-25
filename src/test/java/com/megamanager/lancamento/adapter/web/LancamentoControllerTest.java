package com.megamanager.lancamento.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megamanager.lancamento.adapter.web.dto.EstornarLancamentoRequestDTO;
import com.megamanager.lancamento.adapter.web.dto.LancamentoRequestDTO;
import com.megamanager.lancamento.application.port.in.EstornarLancamentoUseCase;
import com.megamanager.lancamento.application.port.in.ListarLancamentosPorClienteUseCase;
import com.megamanager.lancamento.application.port.in.RegistrarLancamentoUseCase;
import com.megamanager.lancamento.domain.CategoriaLancamento;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;

@WebMvcTest(controllers = LancamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class LancamentoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RegistrarLancamentoUseCase registrarLancamentoUseCase;

    @MockBean
    ListarLancamentosPorClienteUseCase listarLancamentosPorClienteUseCase;

    @MockBean
    EstornarLancamentoUseCase estornarLancamentoUseCase;

    @Test
    void registrar_deveRetornar200EBody() throws Exception {
        LocalDateTime data = LocalDateTime.of(2026, 1, 1, 10, 0);

        Lancamento retorno = Lancamento.reconstruir(
                1L,
                10L,
                data,
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.PAGAMENTO,
                new BigDecimal("300.00"),
                "Pagamento parcial",
                "sistema",
                null
        );

        when(registrarLancamentoUseCase.executar(any(RegistrarLancamentoUseCase.RegistrarLancamentoCommand.class)))
                .thenReturn(retorno);

        LancamentoRequestDTO req = new LancamentoRequestDTO();
        req.setClienteId(10L);
        req.setNatureza("CREDITO");
        req.setCategoria("PAGAMENTO");
        req.setValor(new BigDecimal("300.00"));
        req.setMotivo("Pagamento parcial");
        req.setDataHora(data);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clienteId").value(10))
                .andExpect(jsonPath("$.natureza").value("CREDITO"))
                .andExpect(jsonPath("$.categoria").value("PAGAMENTO"))
                .andExpect(jsonPath("$.valor").value(300.00))
                .andExpect(jsonPath("$.responsavelUsername").value("sistema"));
    }

    @Test
    void listarPorCliente_deveRetornarLista() throws Exception {
        LocalDateTime data = LocalDateTime.of(2026, 1, 1, 10, 0);

        List<Lancamento> lista = List.of(
                Lancamento.reconstruir(1L, 10L, data, NaturezaLancamento.DEBITO, CategoriaLancamento.COBRANCA_ADICIONAL,
                        new BigDecimal("100.00"), "Ensaio", "sistema", null),
                Lancamento.reconstruir(2L, 10L, data.plusDays(1), NaturezaLancamento.CREDITO, CategoriaLancamento.PAGAMENTO,
                        new BigDecimal("50.00"), "Pix", "sistema", null)
        );

        when(listarLancamentosPorClienteUseCase.listarPorCliente(10L)).thenReturn(lista);

        mockMvc.perform(get("/lancamentos/cliente/{clienteId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void estornar_deveRetornar200EBody() throws Exception {
        LocalDateTime data = LocalDateTime.of(2026, 1, 2, 10, 0);

        Lancamento retorno = Lancamento.reconstruir(
                3L,
                10L,
                data,
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.ESTORNO,
                new BigDecimal("100.00"),
                "Duplicado",
                "sistema",
                7L
        );

        when(estornarLancamentoUseCase.executar(any(EstornarLancamentoUseCase.EstornarLancamentoCommand.class)))
                .thenReturn(retorno);

        EstornarLancamentoRequestDTO req = new EstornarLancamentoRequestDTO();
        req.setMotivo("Duplicado");

        mockMvc.perform(post("/lancamentos/{id}/estornar", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoria").value("ESTORNO"))
                .andExpect(jsonPath("$.lancamentoOrigemId").value(7))
                .andExpect(jsonPath("$.motivo").value("Duplicado"));
    }
}
