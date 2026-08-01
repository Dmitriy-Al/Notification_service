package ru.alimovdev.notification_service.model;

import java.sql.Timestamp;

@lombok.Setter
@lombok.Getter
public class User {

    private Long id;
    private int age;
    private String name;
    private String email;
    private Timestamp created_at;

    public User() {}

    @Override
    public String toString() {
        return "\nUser{id=" + id + ", age=" + age +
                ", name='" + name + ", email='" + email +
                ", created_at=" + created_at + '}';
    }

}
