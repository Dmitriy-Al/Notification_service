package ru.alimovdev.notification_service.controller;

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

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public String sendEmail(@RequestBody EmailRequest request) {
        // Передаём данные из запроса в сервис отправки
        emailService.sendEmail(request.getEmail(), request.getSubject(), request.getText());
        return "Email sent";
    }
}
