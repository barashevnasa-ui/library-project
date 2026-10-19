package org.example.library.dto;

//@author Aliaksandra_Baranava

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDto {

    @NotEmpty(message = "Введите email")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotEmpty(message = "Введите пароль")
    private String password;
}
