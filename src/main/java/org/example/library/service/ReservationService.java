package org.example.library.service;

//@author Aliaksandra_Baranava

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.example.library.model.*;
import org.example.library.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ReaderRepository readerRepository;

    @Transactional
    public Reservation bookReservation(Long bookId, Long readerId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена."));
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new EntityNotFoundException("Читатель не найден."));
        if (book.getAvailableCopies() > 0) {
            Reservation reservation = new Reservation(book, reader);
            reservation.setReservationDate(LocalDate.now());
            reservation.setExpirationDate(LocalDate.now().plusDays(5));
            reservation.setStatus(ReservationStatus.PENDING);
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
            return reservationRepository.save(reservation);
        } else throw new IllegalStateException("Нет доступных экземпляров книги '" + book.getTitle() + "' для выдачи.");
    }

    @Transactional
    public Loan reservationIssue(Long reservationId, Long librarianId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Запись о бронировании не найдена."));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Эта бронь не готова к выдаче или уже обработана.");
        }

        Librarian librarian = (Librarian) userRepository.findById(librarianId)
                .orElseThrow(() -> new EntityNotFoundException("Библиотекарь не найден."));

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
        Loan loan = new Loan();
        loan.setBook(reservation.getBook());
        loan.setReader(reservation.getReader());
        loan.setLibrarianIssued(librarian);
        loan.setIssueDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(30));
        loan.setStatus(LoanStatus.ISSUED);
        return loanRepository.save(loan);
    }
}
