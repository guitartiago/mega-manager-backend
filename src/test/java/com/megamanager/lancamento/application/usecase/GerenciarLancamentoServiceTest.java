package com.megamanager.lancamento.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.megamanager.cliente.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.lancamento.application.port.in.EstornarLancamentoUseCase;
import com.megamanager.lancamento.application.port.in.RegistrarLancamentoUseCase;
import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.CategoriaLancamento;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;

@ExtendWith(MockitoExtension.class)
class GerenciarLancamentoServiceTest {

    @Mock
    LancamentoRepository lancamentoRepository;

    @Mock
    ClienteRepository clienteRepository;

    @InjectMocks
    GerenciarLancamentoService service;

    @Test
    void registrar_deveSalvarLancamento_quandoClienteExiste() {
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(mock(Cliente.class)));
        when(lancamentoRepository.salvar(any(Lancamento.class)))
                .thenAnswer(inv -> {
                    Lancamento l = inv.getArgument(0, Lancamento.class);
                    return Lancamento.reconstruir(
                            1L,
                            l.getClienteId(),
                            l.getDataHora(),
                            l.getNatureza(),
                            l.getCategoria(),
                            l.getValor(),
                            l.getMotivo(),
                            l.getResponsavelUsername(),
                            l.getLancamentoOrigemId(),
                            l.getFechamentoId(),
                            l.getDataProcessamento()
                    );
                });

        RegistrarLancamentoUseCase.RegistrarLancamentoCommand cmd = new RegistrarLancamentoUseCase.RegistrarLancamentoCommand(
                10L,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.PAGAMENTO,
                new BigDecimal("300.00"),
                "Pagamento parcial",
                "tiago",
                false
        );

        Lancamento salvo = service.executar(cmd);

        assertNotNull(salvo);
        assertEquals(1L, salvo.getId());
        assertEquals(10L, salvo.getClienteId());
        assertEquals(CategoriaLancamento.PAGAMENTO, salvo.getCategoria());
        assertEquals(NaturezaLancamento.CREDITO, salvo.getNatureza());
        assertEquals(new BigDecimal("300.00"), salvo.getValor());
        assertEquals("tiago", salvo.getResponsavelUsername());

        verify(lancamentoRepository).salvar(any(Lancamento.class));
    }

    @Test
    void registrar_deveDefinirDataHoraAgora_quandoDataHoraNull() {
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(mock(Cliente.class)));
        when(lancamentoRepository.salvar(any(Lancamento.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistrarLancamentoUseCase.RegistrarLancamentoCommand cmd = new RegistrarLancamentoUseCase.RegistrarLancamentoCommand(
                10L,
                null,
                NaturezaLancamento.DEBITO,
                CategoriaLancamento.COBRANCA_ADICIONAL,
                new BigDecimal("100.00"),
                "Ensaio",
                "tiago",
                false
        );

        ArgumentCaptor<Lancamento> captor = ArgumentCaptor.forClass(Lancamento.class);

        Lancamento salvo = service.executar(cmd);

        assertNotNull(salvo.getDataHora());
        verify(lancamentoRepository).salvar(captor.capture());
        assertNotNull(captor.getValue().getDataHora());
    }

    @Test
    void registrar_deveFalhar_quandoClienteNaoExiste() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        RegistrarLancamentoUseCase.RegistrarLancamentoCommand cmd = new RegistrarLancamentoUseCase.RegistrarLancamentoCommand(
                99L,
                LocalDateTime.now(),
                NaturezaLancamento.DEBITO,
                CategoriaLancamento.COBRANCA_ADICIONAL,
                new BigDecimal("50.00"),
                "Taxa",
                "tiago",
                false
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.executar(cmd));
        assertTrue(ex.getMessage().contains("Cliente"));
        verify(lancamentoRepository, never()).salvar(any());
    }

    @Test
    void registrar_deveFalhar_quandoCategoriaEstornoManual() {
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(mock(Cliente.class)));

        RegistrarLancamentoUseCase.RegistrarLancamentoCommand cmd = new RegistrarLancamentoUseCase.RegistrarLancamentoCommand(
                10L,
                LocalDateTime.now(),
                NaturezaLancamento.DEBITO,
                CategoriaLancamento.ESTORNO,
                new BigDecimal("10.00"),
                "",
                "tiago",
                true
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.executar(cmd));
        assertTrue(ex.getMessage().contains("ESTORNO"));
        verify(lancamentoRepository, never()).salvar(any());
    }

    @Test
    void registrar_deveFalhar_quandoDescontoOuCorrecaoESemAdmin() {
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(mock(Cliente.class)));

        RegistrarLancamentoUseCase.RegistrarLancamentoCommand cmd = new RegistrarLancamentoUseCase.RegistrarLancamentoCommand(
                10L,
                LocalDateTime.now(),
                NaturezaLancamento.CREDITO,
                CategoriaLancamento.DESCONTO,
                new BigDecimal("20.00"),
                "Desconto",
                "tiago",
                false
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.executar(cmd));
        assertTrue(ex.getMessage().contains("Apenas ADMIN"));
        verify(lancamentoRepository, never()).salvar(any());
    }

    @Test
    void estornar_deveCriarLancamentoEstorno_invertendoNatureza() {
        Lancamento original = Lancamento.reconstruir(
                7L,
                10L,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                NaturezaLancamento.DEBITO,
                CategoriaLancamento.COBRANCA_ADICIONAL,
                new BigDecimal("100.00"),
                "Ensaio",
                "tiago",
                null,
                null,
                null
        );

        when(lancamentoRepository.buscarPorId(7L)).thenReturn(Optional.of(original));
        when(lancamentoRepository.existeEstornoParaOrigem(7L)).thenReturn(false);
        when(lancamentoRepository.salvar(any(Lancamento.class))).thenAnswer(inv -> inv.getArgument(0));

        EstornarLancamentoUseCase.EstornarLancamentoCommand cmd = new EstornarLancamentoUseCase.EstornarLancamentoCommand(7L, "Duplicado", "admin");

        Lancamento estorno = service.executar(cmd);

        assertEquals(10L, estorno.getClienteId());
        assertEquals(CategoriaLancamento.ESTORNO, estorno.getCategoria());
        assertEquals(NaturezaLancamento.CREDITO, estorno.getNatureza()); // invertido
        assertEquals(new BigDecimal("100.00"), estorno.getValor());
        assertEquals(7L, estorno.getLancamentoOrigemId());
        assertEquals("Duplicado", estorno.getMotivo());
        assertEquals("admin", estorno.getResponsavelUsername());
    }

    @Test
    void estornar_deveFalhar_quandoJaExisteEstorno() {
        Lancamento original = Lancamento.reconstruir(
                7L,
                10L,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                NaturezaLancamento.DEBITO,
                CategoriaLancamento.COBRANCA_ADICIONAL,
                new BigDecimal("100.00"),
                "Ensaio",
                "tiago",
                null,
                null,
                null
        );

        when(lancamentoRepository.buscarPorId(7L)).thenReturn(Optional.of(original));
        when(lancamentoRepository.existeEstornoParaOrigem(7L)).thenReturn(true);

        EstornarLancamentoUseCase.EstornarLancamentoCommand cmd = new EstornarLancamentoUseCase.EstornarLancamentoCommand(7L, "teste", "admin");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.executar(cmd));
        assertTrue(ex.getMessage().toLowerCase().contains("estorno"));
        verify(lancamentoRepository, never()).salvar(any());
    }
}
