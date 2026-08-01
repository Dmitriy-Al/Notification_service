package ru.alimovdev.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.alimovdev.notification_service.api.UserEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(topics = {"user-events"}, partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
public class NotificationIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    void shouldSendEmailWhenUserCreated() throws Exception {
        // Событие в Kafka
        UserEvent event = new UserEvent("CREATE", "test@example.com");
        kafkaTemplate.send("user-events", event);

        // Время для обработки сообщения
        Thread.sleep(2000);

        // Проверка, что mailSender.send был вызван один раз
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}