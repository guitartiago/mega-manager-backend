package com.megamanager.notificacao.domain;

public class EmailMensagem {
    private final String to;
    private final String subject;
    private final String text;
    private final String html;

    public EmailMensagem(String to, String subject, String text, String html) {
        this.to = to; this.subject = subject; this.text = text; this.html = html;
    }
    public String getTo() { return to; }
    public String getSubject() { return subject; }
    public String getText() { return text; }
    public String getHtml() { return html; }
}
