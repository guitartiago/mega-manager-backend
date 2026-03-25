package com.megamanager.lancamento.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.lancamento.application.port.in.EstornarLancamentoUseCase;
import com.megamanager.lancamento.application.port.in.ListarLancamentosPorClienteUseCase;
import com.megamanager.lancamento.application.port.in.RegistrarLancamentoUseCase;
import com.megamanager.lancamento.application.port.out.LancamentoRepository;
import com.megamanager.lancamento.domain.CategoriaLancamento;
import com.megamanager.lancamento.domain.Lancamento;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GerenciarLancamentoService implements RegistrarLancamentoUseCase,
        ListarLancamentosPorClienteUseCase,
        EstornarLancamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(GerenciarLancamentoService.class);

    private final LancamentoRepository lancamentoRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public Lancamento executar(RegistrarLancamentoCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command não pode ser null");
        }

        log.info("➡️ Registrando lançamento: clienteId={}, natureza={}, categoria={}, valor={}",
                command.clienteId(), command.natureza(), command.categoria(), command.valor());

        // valida cliente existente
        clienteRepository.buscarPorId(command.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        if (command.categoria() == CategoriaLancamento.ESTORNO) {
            throw new IllegalArgumentException("Categoria ESTORNO não pode ser registrada manualmente");
        }

        if ((command.categoria() == CategoriaLancamento.DESCONTO || command.categoria() == CategoriaLancamento.CORRECAO)
                && !command.responsavelEhAdmin()) {
            throw new IllegalArgumentException("Apenas ADMIN pode registrar DESCONTO/CORRECAO");
        }

        LocalDateTime data = (command.dataHora() != null) ? command.dataHora() : LocalDateTime.now();

        Lancamento lancamento = Lancamento.criar(
                command.clienteId(),
                data,
                command.natureza(),
                command.categoria(),
                command.valor(),
                command.motivo(),
                command.responsavelUsername()
        );

        Lancamento salvo = lancamentoRepository.salvar(lancamento);
        log.info("✅ Lançamento registrado: id={}", salvo.getId());
        return salvo;
    }

    @Override
    public List<Lancamento> listarPorCliente(Long clienteId) {
        log.info("📋 Listando lançamentos do cliente {}", clienteId);
        return lancamentoRepository.buscarPorCliente(clienteId);
    }

    @Override
    public Lancamento executar(EstornarLancamentoCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command não pode ser null");
        }

        log.info("↩️ Estornando lançamento id={} por {}", command.lancamentoId(), command.responsavelUsername());

        Lancamento original = lancamentoRepository.buscarPorId(command.lancamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Lançamento não encontrado"));

        if (lancamentoRepository.existeEstornoParaOrigem(original.getId())) {
            throw new IllegalArgumentException("Lançamento já possui estorno");
        }

        Lancamento estorno = original.gerarEstorno(command.motivo(), command.responsavelUsername());
        Lancamento salvo = lancamentoRepository.salvar(estorno);

        log.info("✅ Estorno registrado: id={} (origem={})", salvo.getId(), original.getId());
        return salvo;
    }
}
