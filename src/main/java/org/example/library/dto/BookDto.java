package org.example.library.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BookDto {

    @NotBlank(message = "Укажите название книги")
    @Size(max = 255, message = "Название не должно быть длиннее 255 символов")
    private String title;

    @NotBlank(message = "Укажите ISBN")
    @Pattern(regexp = "^[0-9Xx\\- ]{10,17}$", message = "ISBN: от 10 до 17 символов — цифры, X и дефисы")
    private String isbn;

    @NotNull(message = "Укажите год издания")
    @Min(value = 1000, message = "Год издания не может быть раньше 1000")
    @Max(value = 2100, message = "Год издания указан неверно")
    private Integer publicationYear;

    @NotBlank(message = "Укажите издательство")
    private String publisher;

    @NotNull(message = "Укажите количество копий")
    @Min(value = 1, message = "Должна быть хотя бы одна копия")
    @Max(value = 10000, message = "Слишком большое количество копий")
    private Integer totalCopies;

    @NotEmpty(message = "Выберите хотя бы один жанр")
    private List<Long> genresId;

    @NotEmpty(message = "Выберите хотя бы одного автора")
    private List<Long> authorsId;
}
