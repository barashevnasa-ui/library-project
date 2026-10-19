package org.example.library.repository;

//@author Aliaksandra_Baranava

import org.example.library.model.Reservation;
import org.example.library.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatusAndExpirationDateBefore(ReservationStatus status, LocalDate date);

    List<Reservation> findByReaderLibraryCardNumberAndStatus(String cardNumber, ReservationStatus status);

    List<Reservation> findByReaderId(Long readerId);
}
