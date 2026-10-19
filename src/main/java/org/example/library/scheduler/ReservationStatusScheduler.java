package org.example.library.scheduler;

//@author Aliaksandra_Baranava

import org.example.library.model.Book;
import org.example.library.model.Reservation;
import org.example.library.model.ReservationStatus;
import org.example.library.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReservationStatusScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationStatusScheduler.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional
    public void checkAndSetOverdueReservation() {
        LocalDate today = LocalDate.now();

        List<Reservation> expired = reservationRepository
                .findByStatusAndExpirationDateBefore(ReservationStatus.PENDING, today);

        for (Reservation reservation : expired) {
            reservation.setStatus(ReservationStatus.CANCELLED);

            Book book = reservation.getBook();
            int available = book.getAvailableCopies() == null ? 0 : book.getAvailableCopies();
            book.setAvailableCopies(available + 1);
        }

        log.info("Отменено просроченных броней: {}", expired.size());
    }
}