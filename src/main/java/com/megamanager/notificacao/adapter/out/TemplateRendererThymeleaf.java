package com.megamanager.notificacao.adapter.out;

import com.megamanager.notificacao.application.port.out.TemplateRendererPort;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;

public class TemplateRendererThymeleaf implements TemplateRendererPort {

    private final SpringTemplateEngine engine;

    public TemplateRendererThymeleaf(SpringTemplateEngine engine) {
        this.engine = engine;
    }

    @Override
    public String render(String templatePath, Map<String, Object> model, Locale locale) {
        Context ctx = new Context(locale);
        if (model != null) model.forEach(ctx::setVariable);
        return engine.process(templatePath, ctx);
    }
}
