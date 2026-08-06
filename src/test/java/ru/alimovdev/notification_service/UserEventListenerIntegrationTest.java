package ru.alimovdev.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.alimovdev.notification_service.api.UserEvent;
import ru.alimovdev.notification_service.service.EmailService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(topics = {"user-events"}, partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
class UserEventListenerIntegrationTest {

    // В тестах оставил внедрение через @Autowired
    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @MockitoBean
    private EmailService emailService;

    @Test
    void shouldSendEmail_whenUserCreatedEventReceived() throws Exception {
        UserEvent event = new UserEvent("CREATE", "user@example.com");// "CREATE" - Event.CREATE
        // Отправляем событие в Kafka
        kafkaTemplate.send("user-events", event);
        //Время на обработку
        Thread.sleep(2000);

        // Проверка, что EmailService был вызван с правильными параметрами
        verify(emailService, times(1)).sendEmail(
                eq("test@example.com"),
                eq("Аккаунт создан"),
                anyString() // любой текст письма
        );
    }

    @Test
    void shouldSendEmail_whenUserDeletedEventReceived() throws Exception {
        UserEvent event = new UserEvent("DELETE", "test@example.com");// "DELETE" - Event.DELETE

        kafkaTemplate.send("user-events", event);
        Thread.sleep(2000);

        verify(emailService, times(1)).sendEmail(
                eq("test@example.com"),
                eq("Аккаунт удалён"),
                anyString()
        );
    }

    @Test
    void shouldNotSendEmail_whenUnknownOperation() throws Exception {
        UserEvent event = new UserEvent("UNKNOWN", "test@example.com");
        //Время на обработку
        kafkaTemplate.send("user-events", event);
        Thread.sleep(2000);

        // Не должен вызываться EmailService
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}