package ru.alimovdev.notification_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.alimovdev.notification_service.api.EmailRequest;
import ru.alimovdev.notification_service.service.EmailService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    // В тестах оставил внедрение через @Autowired
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Имитация отправки
    @MockitoBean
    private EmailService emailService;

    @Test
    void sendEmail_shouldReturnOk_whenRequestIsValidAndAuthorized() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setEmail("test@example.com");
        request.setSubject("Абв");
        request.setText("А");

        mockMvc.perform(post("/api/notifications/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=")) // admin:password
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent"));
    }

    @Test
    void sendEmail_shouldReturnUnauthorized_whenNoCredentials() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setEmail("test@example.com");
        request.setSubject("Абв");
        request.setText("А");

        mockMvc.perform(post("/api/notifications/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendEmail_shouldReturnBadRequest_whenInvalidEmail() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setEmail("invalid");
        request.setSubject("Абв");
        request.setText("А");

        mockMvc.perform(post("/api/notifications/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Некорректный формат email"));
    }

    @Test
    void sendEmail_shouldReturnBadRequest_whenSubjectIsEmpty() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setEmail("test@example.com");
        request.setSubject("");
        request.setText("А");

        mockMvc.perform(post("/api/notifications/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.subject").value("Тема не может быть пустой"));
    }
}