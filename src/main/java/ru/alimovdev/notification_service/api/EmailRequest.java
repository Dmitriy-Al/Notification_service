package ru.alimovdev.notification_service.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {

    private String email;
    private String subject;
    private String text;

    public EmailRequest() {}

}
