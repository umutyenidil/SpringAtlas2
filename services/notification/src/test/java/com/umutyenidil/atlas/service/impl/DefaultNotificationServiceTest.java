package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.EmailTemplate;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private TemplateEngine templateEngine;
    @InjectMocks
    private DefaultNotificationService notificationService;

    private MimeMessage mimeMessage;

    private final String MOCK_TO_EMAIL = "test@test.com";
    private final String MOCK_HTML_CONTENT = "<html></html>";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "fromEmail", "noreply@atlas.com");

        Session session = Session.getInstance(new Properties());
        mimeMessage = new MimeMessage(session);
    }

    @Test
    @DisplayName("Should process template and send welcome email successfully")
    void sendWelcomeEmail_ShouldSendEmailWithCorrectDetails() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        when(templateEngine.process(eq(EmailTemplate.WELCOME.getPath()), any(Context.class))).thenReturn(MOCK_HTML_CONTENT);

        // Act
        notificationService.sendWelcomeEmail(MOCK_TO_EMAIL);

        // Assert
        verify(templateEngine, times(1)).process(eq(EmailTemplate.WELCOME.getPath()), any(Context.class));

        verify(mailSender, times(1)).send(mimeMessage);

        assertEquals("Welcome to Atlas!", mimeMessage.getSubject());
        assertEquals("noreply@atlas.com", mimeMessage.getFrom()[0].toString());
        assertEquals(MOCK_TO_EMAIL, mimeMessage.getAllRecipients()[0].toString());
    }

    @Test
    @DisplayName("Should handle MessagingException gracefully and not throw to caller")
    void sendWelcomeEmail_WhenMailSenderThrowsException_ShouldCatchAndLog() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(EmailTemplate.WELCOME.getPath()), any(Context.class))).thenReturn(MOCK_HTML_CONTENT);

        doThrow(new MailSendException("Test")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertDoesNotThrow(() -> notificationService.sendWelcomeEmail(MOCK_TO_EMAIL));

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
