package com.megamanager.pix.adapter.out;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

import com.megamanager.pix.application.port.out.PixPayloadBuilderPort;

public class PixPayloadBuilder implements PixPayloadBuilderPort {

    @Override
    public String build(String chavePix, String nome, String cidade, String gui,
                        BigDecimal valor, String txid, String descricao) {
        // Sanitizações simples recomendadas
        nome = safeAscii(nome, 25);
        cidade = safeAscii(cidade, 15);
        descricao = safeAscii(descricao, 50);
        txid = safeAscii(txid, 25);

        // Formata valor: 2 casas, ponto decimal
        String valorStr = valor != null && valor.compareTo(BigDecimal.ZERO) > 0
                ? new DecimalFormat("0.00").format(valor).replace(",", ".")
                : null;

        // Merchant Account Info (ID 26) com subcampos: 00(GUI) 01(chave) 02(descrição opcional)
        String mai = id("00", gui) + id("01", chavePix) + (descricao.isBlank() ? "" : id("02", descricao));
        String merchantAccountInfo = id("26", mai);

        // Additional Data Field Template (ID 62) com TXID (ID 05)
        String additional = id("05", txid);
        String additionalField = id("62", additional);

        // Montagem base sem CRC (ID 63)
        String payloadSemCRC =
                id("00", "01") +                 // Payload Format Indicator
                id("01", "11") +                 // Point of Initiation Method (dinâmico seria 12; estático 11 é aceitável)
                merchantAccountInfo +
                id("52", "0000") +               // Merchant Category Code (default)
                id("53", "986") +                // Currency (986 = BRL)
                (valorStr == null ? "" : id("54", valorStr)) + // Amount (opcional)
                id("58", "BR") +                 // Country Code
                id("59", nome) +                 // Merchant Name
                id("60", cidade) +               // Merchant City
                additionalField +
                "6304";                          // CRC placeholder

        String crc = crc16(payloadSemCRC);
        return payloadSemCRC + crc;
    }

    private static String safeAscii(String s, int max) {
        if (s == null) return "";
        String ascii = s.replaceAll("[^\\p{ASCII}]", "");
        if (ascii.length() > max) return ascii.substring(0, max);
        return ascii;
    }

    // CRC-16/CCITT (polynomial 0x1021, init 0xFFFF)
    private static String crc16(String input) {
        int polynomial = 0x1021;
        int result = 0xFFFF;
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        for (byte b : bytes) {
            result ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((result & 0x8000) != 0) result = (result << 1) ^ polynomial;
                else result <<= 1;
                result &= 0xFFFF;
            }
        }
        return String.format("%04X", result & 0xFFFF);
    }

    // helper local (método aninhado permitido desde Java 11 via truque? -> trocamos por método estático)
    private static String id(String id, String value) {
        return id + String.format("%02d", value.getBytes(StandardCharsets.UTF_8).length) + value;
    }
}
