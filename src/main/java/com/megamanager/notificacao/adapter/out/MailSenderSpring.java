package com.megamanager.notificacao.adapter.out;

import com.megamanager.notificacao.application.port.out.MailSenderPort;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailSenderSpring implements MailSenderPort {

    private final JavaMailSender mailSender;
    private final String from;

    public MailSenderSpring(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(String to, String subject, String textFallback, String html, Attachment... attachments) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(textFallback, html);

            if (attachments != null) {
                for (Attachment a : attachments) {
                    if (a == null) continue;
                    if (a.inline()) {
                        helper.addInline(a.contentId(), new ByteArrayResource(a.content()), a.contentType());
                    } else {
                        helper.addAttachment(a.filename(), new ByteArrayResource(a.content()), a.contentType());
                    }
                }
            }
            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar e-mail", e);
        }
    }
}
