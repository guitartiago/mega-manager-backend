package com.megamanager.consumo.application.usecase;

import com.megamanager.cliente.application.port.out.ClienteRepository;
import com.megamanager.consumo.application.port.in.PagarContaClienteUseCase;
import com.megamanager.consumo.application.port.out.ConsumoRepository;

public class PagarContaClienteService implements PagarContaClienteUseCase {

    private final ConsumoRepository consumoRepository;
    private final ClienteRepository clienteRepository;

    public PagarContaClienteService(ConsumoRepository consumoRepository,
                                     ClienteRepository clienteRepository) {
        this.consumoRepository = consumoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void pagarConta(Long clienteId) {
        boolean clienteExiste = clienteRepository.buscarPorId(clienteId).isPresent();
        if (!clienteExiste) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }

        consumoRepository.marcarConsumosComoPagos(clienteId);
    }
}
