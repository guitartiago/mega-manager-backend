package com.megamanager.consumo.application.port.out;

import com.megamanager.consumo.domain.Consumo;

import java.util.List;

public interface ConsumoRepository {

    Consumo salvar(Consumo consumo);

    List<Consumo> buscarPorCliente(Long clienteId);
}
