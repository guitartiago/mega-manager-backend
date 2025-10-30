package com.megamanager.pix.adapter.out;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.megamanager.pix.application.port.out.QrCodeEncoderPort;

import java.io.ByteArrayOutputStream;

public class QrCodeEncoderZXing implements QrCodeEncoderPort {
    @Override
    public byte[] encode(String payload, int sizePx) {
        try {
            var writer = new QRCodeWriter();
            var matrix = writer.encode(payload, BarcodeFormat.QR_CODE, sizePx, sizePx);
            try (var baos = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar QRCode", e);
        }
    }
}
