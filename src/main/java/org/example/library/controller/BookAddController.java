package org.example.library.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.example.library.dto.AuthorDto;
import org.example.library.dto.BookDto;
import org.example.library.dto.WriteOffDto;
import org.example.library.model.Author;
import org.example.library.model.Book;
import org.example.library.repository.AuthorRepository;
import org.example.library.repository.GenreRepository;
import org.example.library.service.AuthorService;
import org.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/* Добавление книги в каталог (доступно только библиотекарю). */
@Controller
public class BookAddController {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthorService authorService;

    @GetMapping("/book-add")
    public String showForm(Model model) {
        model.addAttribute("book", new BookDto());
        fillReferenceData(model);
        return "book-add";
    }

    @PostMapping("/book-add")
    public String addBook(@Valid @ModelAttribute("book") BookDto bookDto,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            fillReferenceData(model);
            return "book-add";
        }
        try {
            Book saved = bookService.addBook(bookDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Книга «" + saved.getTitle() + "» добавлена в каталог.");
            return "redirect:/librarian";

        } catch (IllegalStateException | EntityNotFoundException e) {
            model.addAttribute("globalError", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Не удалось добавить книгу.");
        }
        fillReferenceData(model);
        return "book-add";
    }

    @GetMapping("/write-off")
    public String showWriteOffForm(Model model) {
        model.addAttribute("writeOff", new WriteOffDto());
        return "write-off";
    }

    @PostMapping("/write-off")
    public String writeOffBook(@Valid @ModelAttribute("writeOff") WriteOffDto writeOffDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "write-off";
        }
        try {
            bookService.writeOffBook(writeOffDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Списано копий: " + writeOffDto.getAmount() + ".");
            return "redirect:/librarian";

        } catch (IllegalStateException | EntityNotFoundException e) {
            model.addAttribute("globalError", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Произошла непредвиденная ошибка при списании.");
        }
        return "write-off";
    }

    private void fillReferenceData(Model model) {
        model.addAttribute("authors",
                authorRepository.findAll(Sort.by(Sort.Order.asc("name").ignoreCase())));
        model.addAttribute("genres",
                genreRepository.findAll(Sort.by(Sort.Order.asc("name").ignoreCase())));
    }

    @GetMapping("/author-add")
    public String showFormAuthor(Model model) {
        model.addAttribute("author", new AuthorDto());
        return "author-add";
    }

    @PostMapping("/author-add")
    public String addAuthor(@Valid @ModelAttribute("author") AuthorDto authorDto,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "author-add";
        }
        try {
            Author saved = authorService.addAuthor(authorDto.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Автор «" + saved.getName() + "» добавлен.");
            return "redirect:/author-add";

        } catch (IllegalStateException e) {
            model.addAttribute("globalError", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Не удалось добавить автора.");
        }
        return "author-add";
    }
}
