package com.megamanager.pix.application.port.out;

public interface QrCodeEncoderPort {
    byte[] encode(String payload, int sizePx);
}
