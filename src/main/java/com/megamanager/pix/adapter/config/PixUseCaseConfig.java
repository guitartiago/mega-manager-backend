package com.megamanager.pix.adapter.config;

import com.megamanager.pix.adapter.out.PixPayloadBuilder;
import com.megamanager.pix.adapter.out.QrCodeEncoderZXing;
import com.megamanager.pix.application.port.in.GerarPixQrCodeUseCase;
import com.megamanager.pix.application.port.out.PixPayloadBuilderPort;
import com.megamanager.pix.application.port.out.QrCodeEncoderPort;
import com.megamanager.pix.application.usecase.GerarPixQrCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PixUseCaseConfig {

    @Bean
    public PixPayloadBuilderPort pixPayloadBuilderPort() {
        return new PixPayloadBuilder();
    }

    @Bean
    public QrCodeEncoderPort qrCodeEncoderPort() {
        return new QrCodeEncoderZXing();
    }

    @Bean
    public GerarPixQrCodeUseCase gerarPixQrCodeUseCase(
            @Value("${pix.chave}") String chave,
            @Value("${pix.nome}") String nome,
            @Value("${pix.cidade}") String cidade,
            @Value("${pix.gui:BR.GOV.BCB.PIX}") String gui,
            PixPayloadBuilderPort payloadBuilder,
            QrCodeEncoderPort qrCodeEncoder) {
        return new GerarPixQrCodeService(chave, nome, cidade, gui, payloadBuilder, qrCodeEncoder);
    }
}
