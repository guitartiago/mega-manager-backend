package com.megamanager.notificacao.application.usecase;

import com.megamanager.notificacao.application.port.in.EnviarContaPixEmailUseCase;
import com.megamanager.notificacao.application.port.out.MailSenderPort;
import com.megamanager.notificacao.application.port.out.TemplateRendererPort;
import com.megamanager.pix.application.port.in.GerarPixQrCodeUseCase;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class EnviarContaPixEmailService implements EnviarContaPixEmailUseCase {

    private final MailSenderPort mailSender;
    private final TemplateRendererPort renderer;
    private final GerarPixQrCodeUseCase gerarPix;

    public EnviarContaPixEmailService(MailSenderPort mailSender,
                                      TemplateRendererPort renderer,
                                      GerarPixQrCodeUseCase gerarPix) {
        this.mailSender = mailSender;
        this.renderer = renderer;
        this.gerarPix = gerarPix;
    }

    @Override
    public void execute(Command cmd) {
        var resPix = gerarPix.execute(new GerarPixQrCodeUseCase.Command(
                cmd.valor(), cmd.descricao()));

        String valorBRL = formatarBRL(cmd.valor());
        String copiaCola = resPix.payload();

        var model = new HashMap<String, Object>();
        model.put("clienteNome", cmd.nomeCliente());
        model.put("saudacaoOpcional", "Segue o Pix para pagamento da sua conta.");
        model.put("valorBRL", valorBRL);
        model.put("descricao", cmd.descricao() == null ? "" : cmd.descricao());
        model.put("txid", resPix.txid());
        model.put("copiaCola", copiaCola);
        // opcional: model.put("ctaUrl", "https://megafuzz.local/contas/" + resPix.txid());

        String html = renderer.render("email/conta-pix.html", model, new Locale("pt", "BR"));
        String text = """
                Olá, %s!

                Valor: %s
                Descrição: %s
                TXID: %s

                Pix (copia e cola):
                %s
                """.formatted(
                cmd.nomeCliente(),
                valorBRL,
                cmd.descricao() == null ? "" : cmd.descricao(),
                resPix.txid(),
                copiaCola
        );

        var inlineQr = MailSenderPort.Attachment.inline("qrcode", resPix.png(), "image/png");

        if (cmd.anexarArquivos()) {
            var attachPng = MailSenderPort.Attachment.file("qrcode-pix.png", resPix.png(), "image/png");
            var attachTxt = MailSenderPort.Attachment.file("pix-copia-e-cola.txt", copiaCola.getBytes(), "text/plain; charset=UTF-8");
            mailSender.send(cmd.emailDestino(), "Sua conta no MegaFuzz - Pix para pagamento", text, html,
                    inlineQr, attachPng, attachTxt);
        } else {
            mailSender.send(cmd.emailDestino(), "Sua conta no MegaFuzz - Pix para pagamento", text, html, inlineQr);
        }
    }

    private static String formatarBRL(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }
}
