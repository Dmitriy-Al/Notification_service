package ru.alimovdev.notification_service.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {

    // Валидацию решил сделать через аннотаций, как наиболее органичный способ для Spring
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Size(max = 50, message = "Email не должен превышать 50 символов")
    private String email;

    @NotBlank(message = "Тема не может быть пустой")
    @Size(max = 200, message = "Тема не должна превышать 200 символов")
    private String subject;

    @NotBlank(message = "Текст письма не может быть пустым")
    @Size(max = 5000, message = "Текст письма не должен превышать 5000 символов")
    private String text;

    public EmailRequest() {}

}
