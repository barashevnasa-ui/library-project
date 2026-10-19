package org.example.library.service;

//@author Aliaksandra_Baranava

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.example.library.dto.LoanDto;
import org.example.library.dto.LoanReturnDto;
import org.example.library.model.*;
import org.example.library.repository.BookRepository;
import org.example.library.repository.LoanRepository;
import org.example.library.repository.ReaderRepository;
import org.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReaderRepository readerRepository;

    @Transactional
    public Loan loanIssue(LoanDto loanDto, Long librarianIssuedId) {
        Reader reader = readerRepository.findByLibraryCardNumber(loanDto.getLibraryCardNumber())
                .orElseThrow(() -> new EntityNotFoundException("Читатель не найден"));

        Librarian librarianIssued = (Librarian) userRepository.findById(librarianIssuedId)
                .orElseThrow(() -> new EntityNotFoundException("Библиотекарь не найден"));

        Book book = bookRepository.findByIsbn(loanDto.getBookIsbn())
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
        if (book.getAvailableCopies() > 0) {
            Loan loan = new Loan(book, reader, librarianIssued);
            loan.setIssueDate(LocalDate.now());
            loan.setDueDate(LocalDate.now().plusDays(30));
            loan.setStatus(LoanStatus.ISSUED);
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
            return loanRepository.save(loan);
        } else throw new IllegalStateException("Нет доступных экземпляров книги '" + book.getTitle() + "' для выдачи.");
    }

    @Transactional
    public Long loanReturn(LoanReturnDto loanReturnDto, Long librarianReceivedId) {
        Loan loan = loanRepository.findByReaderLibraryCardNumberAndBookIsbn(loanReturnDto.getLibraryCardNumber(), loanReturnDto.getBookIsbn())
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена."));
        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException("Эта книга уже возвращена.");
        }
        Librarian librarianReceived = (Librarian) userRepository.findById(librarianReceivedId)
                .orElseThrow(() -> new EntityNotFoundException("Библиотекарь не найден"));
        loan.setReturnDate(LocalDate.now());
        Long overDueDays = 0L;
        if (loan.getStatus().equals(LoanStatus.OVERDUE)) {
            overDueDays = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
        }
        loan.setStatus(LoanStatus.RETURNED);
        loan.setLibrarianReceived(librarianReceived);
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        loanRepository.save(loan);
        return overDueDays;
    }

}
