package org.example.library.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.dto.BookDto;
import org.example.library.model.Author;
import org.example.library.model.Book;
import org.example.library.model.Genre;
import org.example.library.repository.AuthorRepository;
import org.example.library.repository.BookRepository;
import org.example.library.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    @Transactional
    public Book addBook(BookDto dto) {
        String isbn = dto.getIsbn().trim();
        if (bookRepository.existsByIsbn(isbn)) {
            throw new IllegalStateException("Книга с таким ISBN уже есть в каталоге.");
        }

        List<Author> authors = authorRepository.findAllById(dto.getAuthorsId());
        if (authors.size() != new HashSet<>(dto.getAuthorsId()).size()) {
            throw new EntityNotFoundException("Один или несколько выбранных авторов не найдены.");
        }
        List<Genre> genres = genreRepository.findAllById(dto.getGenresId());
        if (genres.size() != new HashSet<>(dto.getGenresId()).size()) {
            throw new EntityNotFoundException("Один или несколько выбранных жанров не найдены.");
        }

        Book book = new Book();
        book.setTitle(dto.getTitle().trim());
        book.setIsbn(isbn);
        book.setPublicationYear(dto.getPublicationYear());
        book.setPublisher(dto.getPublisher().trim());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies()); // новая книга: все копии свободны
        book.setAuthors(authors);
        book.setGenres(genres);
        return bookRepository.save(book);
    }
}
