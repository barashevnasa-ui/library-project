package org.example.library.model;

//@author Aliaksandra_Baranava

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
     private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", nullable = false)
     private Reader reader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "librarian_issued_id", nullable = false)
     private Librarian librarianIssued;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "librarian_received_id")
     private Librarian librarianReceived;

     @Column(nullable = false)
     private LocalDate issueDate;

     @Column(nullable = false)
     private LocalDate dueDate;

     private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
     @Column(nullable = false)
     private LoanStatus status;

    public Loan(Book book, Reader reader, Librarian librarian){
        this.book = book;
        this.reader = reader;
        this.librarianIssued = librarian;
    }
}
