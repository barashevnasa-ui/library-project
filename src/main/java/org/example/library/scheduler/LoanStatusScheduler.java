package org.example.library.scheduler;

//@author Aliaksandra_Baranava

import jakarta.transaction.Transactional;
import org.example.library.model.Loan;
import org.example.library.model.LoanStatus;
import org.example.library.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class LoanStatusScheduler {

    @Autowired
    private LoanRepository loanRepository;

    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional
    public void checkAndSetOverdueLoan() {
        LocalDate today = LocalDate.now();
       loanRepository.updateStatusToOverdue(today);
    }

}
