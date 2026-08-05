package ru.alimovdev.notification_service.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.alimovdev.notification_service.api.EmailRequest;
import ru.alimovdev.notification_service.service.EmailService;

//  Контроллер для отправки писем по HTTP-запросу
@Slf4j
@RestController
@RequestMapping("/api/notifications")  // путь для всех эндпоинтов контроллера
public class NotificationController {

    private final EmailService emailService;

    @Autowired
    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }
    @PostMapping("/send-email")
    public String sendEmail(@Valid @RequestBody EmailRequest request) { // @Valid: если данные не валидны, вызывается GlobalExceptionHandler.handleValidateExceptions()
        // Передача данных из запроса в сервис отправки
        emailService.sendEmail(request.getEmail(), request.getSubject(), request.getText());
        return "Email sent";
    }
}
