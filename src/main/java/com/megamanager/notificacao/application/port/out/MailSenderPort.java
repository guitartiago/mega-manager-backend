package com.megamanager.notificacao.application.port.out;

public interface MailSenderPort {
    void send(String to, String subject, String textFallback, String html,
              Attachment... attachments);

    record Attachment(String filename, byte[] content, String contentType, boolean inline, String contentId) {
        public static Attachment inline(String cid, byte[] bytes, String contentType) {
            return new Attachment(null, bytes, contentType, true, cid);
        }
        public static Attachment file(String name, byte[] bytes, String contentType) {
            return new Attachment(name, bytes, contentType, false, null);
        }
    }
}
