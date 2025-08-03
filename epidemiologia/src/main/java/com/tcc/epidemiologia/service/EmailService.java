package com.tcc.epidemiologia.service;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tcc.epidemiologia.domain.Alerta;

import jakarta.mail.MessagingException;

@Service
public class EmailService {

    private JavaMailSender mailSender;
    private static final Logger logger = LogManager.getLogger(EmailService.class);
    private static String subject = "Alerta de Surto Detectado";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("mailExecutor")
    @Retryable(
        value = { MailException.class, MessagingException.class },
        maxAttempts = 3, 
        backoff = @org.springframework.retry.annotation.Backoff(delay = 5000, multiplier = 2)
    )
    public void send(String to, Alerta alerta) throws MessagingException {
        logger.info("Enviando email de alerta para " + to + " sobre alerta: " + alerta.tipo() + " no bairro " + alerta.bairro());
        var mimeMsg = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(mimeMsg, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody(alerta), true);
        mailSender.send(mimeMsg);
        logger.info("Email enviado com sucesso para " + to);
  }

  @Recover
  public void recover(MessagingException ex, String to, Alerta alerta) {
      logger.error("Falha ao enviar email para " + to + ": " + ex.getMessage());
  }

  private String htmlBody(Alerta alerta) {
      String[] partes = alerta.qtdCasosSemanais().split(", ");

      StringBuilder casosHtml = new StringBuilder();
      for (String parte : partes) {
        casosHtml.append("<p style=\"margin:4px 0;\">")
          .append(parte)
          .append("</p>");
      }

      return """
          <!DOCTYPE html>
          <html lang="pt-BR">
          <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Alerta de Surto</title>
          </head>
          <body style="margin:0;padding:0;font-family:Arial,sans-serif;background:#f4f4f6;">
            <table width="100%%" cellpadding="0" cellspacing="0" border="0">
              <tr>
                <td align="center" style="padding:20px 0;">
                  <table width="600" cellpadding="0" cellspacing="0" border="0"
                        style="background:#ffffff;border-radius:8px;overflow:hidden;">
                    <tr style="background:#ff4d4d;color:#ffffff;">
                      <td style="padding:16px;font-size:1.25em;">
                        🚨 Alerta de Surto Detectado
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:20px;color:#333333;line-height:1.5;">
                        <p><strong>Tipo:</strong> %s</p>
                        <p><strong>Bairro:</strong> %s</p>
                        <p><strong>Data/Hora:</strong> %s</p>
                        <!-- Aqui entram as linhas de casos -->
                        %s
                        <hr style="border:none;border-top:1px solid #e0e0e0;margin:20px 0;">
                        <p>Por favor, avalie imediatamente e tome as providências necessárias.</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="background:#f9f9f9;color:#777777;
                                font-size:0.85em;padding:12px;text-align:center;">
                        Este é um e-mail automático. Não responda a esta mensagem.
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </body>
          </html>
          """
          .formatted(
              alerta.tipo(),
              alerta.bairro(),
              new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(alerta.horarioAlerta()),
              casosHtml.toString());
  }

}
