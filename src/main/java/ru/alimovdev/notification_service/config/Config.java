package ru.alimovdev.notification_service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
public class Config {

    @Value("${spring.mail.username}")
    public String mailUsername;

}