package com.megamanager.notificacao.application.port.out;

import java.util.Locale;
import java.util.Map;

public interface TemplateRendererPort {
    String render(String templatePath, Map<String, Object> model, Locale locale);
}
