package com.megamanager.pix.adapter.web;

import com.megamanager.pix.application.port.in.GerarPixQrCodeUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/pix")
public class PixController {

    private final GerarPixQrCodeUseCase gerarPixQrCode;

    public PixController(GerarPixQrCodeUseCase gerarPixQrCode) {
        this.gerarPixQrCode = gerarPixQrCode;
    }

    // Retorna PNG do QR Code
    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qrcode(@RequestParam BigDecimal valor,
                                         @RequestParam(required = false, defaultValue = "") String descricao) {
        var res = gerarPixQrCode.execute(new GerarPixQrCodeUseCase.Command(valor, descricao));
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .body(res.png());
    }

    // Retorna payload EMV (texto)
    @GetMapping(value = "/payload", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> payload(@RequestParam BigDecimal valor,
                                          @RequestParam(required = false, defaultValue = "") String descricao) {
        var res = gerarPixQrCode.execute(new GerarPixQrCodeUseCase.Command(valor, descricao));
        return ResponseEntity.ok(res.payload());
    }

    // Retorna infos úteis em JSON (txid e payload)
    @GetMapping("/info")
    public ResponseEntity<?> info(@RequestParam BigDecimal valor,
                                  @RequestParam(required = false, defaultValue = "") String descricao) {
        var res = gerarPixQrCode.execute(new GerarPixQrCodeUseCase.Command(valor, descricao));
        return ResponseEntity.ok(new InfoDTO(res.txid(), res.payload()));
    }

    private record InfoDTO(String txid, String payload) {}
}
