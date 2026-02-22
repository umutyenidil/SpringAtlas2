package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.enumeration.EmailTemplate;
import com.umutyenidil.atlas.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DefaultNotificationService implements NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@atlas.com}")
    private String fromEmail;

    public DefaultNotificationService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendWelcomeEmail(String toEmail) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("email", toEmail);
        try {
            this.sendEmail(toEmail, "Welcome to Atlas!", EmailTemplate.WELCOME, variables);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    @Async
    protected void sendEmail(String to, String subject, EmailTemplate template, Map<String, Object> variables) throws MessagingException {
        Context context = new Context();
        if (variables != null && !variables.isEmpty()) {
            context.setVariables(variables);
        }

        String htmlContent = templateEngine.process(template.getPath(), context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }
}
