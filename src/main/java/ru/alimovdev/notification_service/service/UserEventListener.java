package ru.alimovdev.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.alimovdev.notification_service.api.UserEvent;
import static ru.alimovdev.notification_service.api.Event.*;

@Slf4j
@Component
public class UserEventListener {

    private final EmailService emailService;

    @Autowired
    public UserEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /*   @KafkaListener - добавляется к методу, который содержит логику обработки сообщения и Spring
    автоматически настраивает инфраструктуру, чтобы этот метод вызывался при появлении сообщения в Kafka
    handleUserEvent() - метод-обработчик сообщений из Kafka, автоматически вызывается при поступлении нового
    сообщения в топик "user-events".  */
    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        // email и тип операции из события
        String email = event.getEmail();
        String operation = event.getOperation();
        String subject;
        String text;

        // В зависимости от операции формируется тема и текст письма
        if (CREATE.getEvent().equals(operation)) {
            subject = "Аккаунт создан";
            text = "Здравствуйте! Ваш аккаунт на сайте alimovdev.ru был успешно создан.";
        } else if (DELETE.getEvent().equals(operation)) {
            subject = "Аккаунт удалён";
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            log.error("Incorrect event value: " + operation);
            return;
        }
        // Отправка письма через EmailService
        emailService.sendEmail(email, subject, text);
    }
}
