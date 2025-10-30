package com.megamanager.notificacao.adapter.config;

import com.megamanager.notificacao.adapter.out.MailSenderSpring;
import com.megamanager.notificacao.adapter.out.TemplateRendererThymeleaf;
import com.megamanager.notificacao.application.port.in.EnviarContaPixEmailUseCase;
import com.megamanager.notificacao.application.port.out.MailSenderPort;
import com.megamanager.notificacao.application.port.out.TemplateRendererPort;
import com.megamanager.notificacao.application.usecase.EnviarContaPixEmailService;
import com.megamanager.pix.application.port.in.GerarPixQrCodeUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailUseCaseConfig {

    @Bean
    public MailSenderPort mailSenderPort(JavaMailSender springSender,
                                         @Value("${mail.from:nao-responder@megafuzz.com.br}") String from) {
        return new MailSenderSpring(springSender, from);
    }

    @Bean
    public TemplateRendererPort templateRendererPort(SpringTemplateEngine engine) {
        return new TemplateRendererThymeleaf(engine);
    }

    @Bean
    public EnviarContaPixEmailUseCase enviarContaPixEmailUseCase(
            MailSenderPort mailSenderPort,
            TemplateRendererPort templateRendererPort,
            GerarPixQrCodeUseCase gerarPixQrCodeUseCase) {

        return new EnviarContaPixEmailService(mailSenderPort, templateRendererPort, gerarPixQrCodeUseCase);
    }
}
