package com.tcc.epidemiologia;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import com.tcc.epidemiologia.domain.Alerta;
import com.tcc.epidemiologia.service.EmailService;

import jakarta.mail.internet.MimeMessage;

public class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setup() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    @Test
    void deveEnviarEmailComSucesso() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        Alerta alerta = Alerta.create(
                System.currentTimeMillis(),
                "Sepse",
                "Centro",
                123L,
                5, 3, 2, 1);

        emailService.send("testeEmail@gmail.com", alerta);

        verify(mailSender).send(mimeMessage);
    }

}
