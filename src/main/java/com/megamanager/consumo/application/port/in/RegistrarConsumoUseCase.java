package com.megamanager.consumo.application.port.in;

import com.megamanager.consumo.domain.Consumo;

public interface RegistrarConsumoUseCase {
    Consumo registrar(Consumo consumo);
}
