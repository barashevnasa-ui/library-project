package org.example.library.repository;

//@author Aliaksandra_Baranava

import org.example.library.model.Loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    String UPDATE_OVERDUE_QUERY = """
        UPDATE Loan l
        SET l.status = org.example.library.model.LoanStatus.OVERDUE
        WHERE l.status = org.example.library.model.LoanStatus.ISSUED
        AND l.dueDate < :today
        """;

    Optional<Loan> findByReaderLibraryCardNumberAndBookIsbn(String libraryCardNumber, String isbn);

    @Modifying
    @Query(UPDATE_OVERDUE_QUERY)
    void updateStatusToOverdue(@Param("today") LocalDate today);

    List<Loan> findByReaderIdAndReturnDateIsNullOrderByDueDateAsc(Long readerId);
}
