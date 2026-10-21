package org.example.library.dto;

//@author Aliaksandra_Baranava

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WriteOffDto {

    @NotBlank(message = "Укажите книгу")
    private String bookIsbn;

    @NotNull(message = "Укажите количество для списания")
    @Min(value = 1, message = "Должна быть хотя бы одна копия")
    @Max(value = 10000, message = "Слишком большое количество копий")
    private Integer amount;
}
