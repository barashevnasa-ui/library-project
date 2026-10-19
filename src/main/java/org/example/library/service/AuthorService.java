package org.example.library.service;

import org.example.library.model.Author;
import org.example.library.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public Author addAuthor(String rawName) {
        String name = rawName.trim().replaceAll("\\s+", " ");
        if (authorRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalStateException("Автор «" + name + "» уже есть в базе.");
        }
        Author author = new Author();
        author.setName(name);
        return authorRepository.save(author);
    }
}