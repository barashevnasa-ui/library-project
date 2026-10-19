package org.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorDto {

    @NotBlank(message = "Введите имя автора")
    @Size(max = 255, message = "Не более 255 символов")
    private String name;
}