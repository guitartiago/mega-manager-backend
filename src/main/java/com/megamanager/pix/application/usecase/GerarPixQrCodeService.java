package com.megamanager.pix.application.usecase;

import com.megamanager.pix.application.port.in.GerarPixQrCodeUseCase;
import com.megamanager.pix.application.port.out.PixPayloadBuilderPort;
import com.megamanager.pix.application.port.out.QrCodeEncoderPort;

import java.math.BigDecimal;
import java.util.UUID;

public class GerarPixQrCodeService implements GerarPixQrCodeUseCase {

    private final String chave;
    private final String nome;
    private final String cidade;
    private final String gui;
    private final PixPayloadBuilderPort payloadBuilder;
    private final QrCodeEncoderPort qrCodeEncoder;

    public GerarPixQrCodeService(String chave, String nome, String cidade, String gui,
                                 PixPayloadBuilderPort payloadBuilder,
                                 QrCodeEncoderPort qrCodeEncoder) {
        this.chave = chave;
        this.nome = nome;
        this.cidade = cidade;
        this.gui = gui;
        this.payloadBuilder = payloadBuilder;
        this.qrCodeEncoder = qrCodeEncoder;
    }

    @Override
    public Result execute(Command command) {
        BigDecimal valor = command.valor() == null ? BigDecimal.ZERO : command.valor();
        String descricao = command.descricao() == null ? "" : command.descricao();

        // TXID: máx. 25 chars (recomendação BCB). Ex.: MEGA + 12 chars aleatórios
        String txid = "MEGA" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        String payload = payloadBuilder.build(chave, nome, cidade, gui, valor, txid, descricao);
        byte[] png = qrCodeEncoder.encode(payload, 360);
        return new Result(txid, payload, png);
    }
}
