package org.example.library.dto;

//@author Aliaksandra_Baranava

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoanDto {
    @NotBlank(message = "Укажите книгу")
    private String bookIsbn;

    @NotBlank(message = "Укажите читателя")
    private String libraryCardNumber;

}
