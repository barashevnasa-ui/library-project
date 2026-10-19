package org.example.library.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationReaderDto {

    public static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*\\(\\),\\.\\?\\\":\\{\\}\\|<>_])[A-Za-z\\d!@#\\$%\\^&\\*\\(\\),\\.\\?\\\":\\{\\}\\|<>_]{8,20}$";

    @NotBlank(message = "Имя обязательно для заполнения")
    @Size(min = 2, max = 16, message = "Имя должно содержать от 2 до 16 букв")
    @Pattern(regexp = "^[\\p{L}\\-]+$", message = "Имя может содержать только буквы и дефис")
    private String firstname;

    @NotBlank(message = "Фамилия обязательна для заполнения")
    @Size(min = 2, max = 16, message = "Фамилия должна содержать от 2 до 16 букв")
    @Pattern(regexp = "^[\\p{L}\\-]+$", message = "Фамилия может содержать только буквы и дефис")
    private String lastname;

    @NotBlank(message = "Пароль обязателен")
    @Pattern(regexp = PASSWORD_REGEX,
            message = "Пароль: 8–20 символов, латинские буквы, цифры и хотя бы один спецсимвол")
    private String password;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат электронной почты")
    @Size(min = 5, max = 50, message = "Email должен быть от 5 до 50 символов")
    private String email;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+375(25|29|33|44)\\d{7}$", message = "Номер телефона: +375 (25|29|33|44) и 7 цифр")
    private String phonenumber;

}
