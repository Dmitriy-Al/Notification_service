package ru.alimovdev.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.alimovdev.notification_service.config.Config;

@Slf4j
@Service
public class EmailService {
    // JavaMailSender - интерфейс из Spring Framework, абстрагирует работу с JavaMail API
    private final JavaMailSender mailSender; // бин для отправки писем
    private final Config config;

    @Autowired
    public EmailService(JavaMailSender mailSender, Config config) {
        this.mailSender = mailSender;
        this.config = config;
    }

    public void sendEmail(String to, String subject, String text) {
        // SimpleMailMessage — класс из Spring Framework, который моделирует простое электронное письмо.
        // Он включает такие поля, как «от», «кому», «cc», «тема» и текст.
        SimpleMailMessage message = new SimpleMailMessage();

        // Добавление отправителя
        message.setFrom(config.mailUsername);
        // Добавление получателя
        message.setTo(to);
        // Тема
        message.setSubject(subject);
        // Текст письма
        message.setText(text);

        try {
            // Отправка письма через JavaMailSender
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (MailException e) {
            log.error("Failed to send email to {}: {}", to, e);
        }

    }
}