package com.megamanager.lancamento.adapter.web.mapper;

import com.megamanager.lancamento.adapter.web.dto.LancamentoRequestDTO;
import com.megamanager.lancamento.adapter.web.dto.LancamentoResponseDTO;
import com.megamanager.lancamento.application.port.in.RegistrarLancamentoUseCase;
import com.megamanager.lancamento.domain.CategoriaLancamento;
import com.megamanager.lancamento.domain.Lancamento;
import com.megamanager.lancamento.domain.NaturezaLancamento;

public class LancamentoDtoMapper {

    private LancamentoDtoMapper() {}

    public static RegistrarLancamentoUseCase.RegistrarLancamentoCommand toCommand(LancamentoRequestDTO dto,
                                                                                  String responsavelUsername,
                                                                                  boolean responsavelEhAdmin) {
        return new RegistrarLancamentoUseCase.RegistrarLancamentoCommand(
                dto.getClienteId(),
                dto.getDataHora(),
                NaturezaLancamento.valueOf(dto.getNatureza()),
                CategoriaLancamento.valueOf(dto.getCategoria()),
                dto.getValor(),
                dto.getMotivo(),
                responsavelUsername,
                responsavelEhAdmin
        );
    }

    public static LancamentoResponseDTO toResponse(Lancamento dominio) {
        LancamentoResponseDTO dto = new LancamentoResponseDTO();
        dto.setId(dominio.getId());
        dto.setClienteId(dominio.getClienteId());
        dto.setDataHora(dominio.getDataHora());
        dto.setNatureza(dominio.getNatureza().name());
        dto.setCategoria(dominio.getCategoria().name());
        dto.setValor(dominio.getValor());
        dto.setMotivo(dominio.getMotivo());
        dto.setResponsavelUsername(dominio.getResponsavelUsername());
        dto.setLancamentoOrigemId(dominio.getLancamentoOrigemId());
        return dto;
    }
}
