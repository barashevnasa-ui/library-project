package org.example.library.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.example.library.dto.LoanDto;
import org.example.library.dto.LoanReturnDto;
import org.example.library.model.Book;
import org.example.library.model.Loan;
import org.example.library.model.Reservation;
import org.example.library.model.ReservationStatus;
import org.example.library.repository.BookRepository;
import org.example.library.repository.LoanRepository;
import org.example.library.repository.ReservationRepository;
import org.example.library.security.LibraryUserDetails;
import org.example.library.service.LoanService;
import org.example.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BookController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private LoanRepository loanRepository;

    @GetMapping("/issue")
    public String showLoanPage(Model model) {
        model.addAttribute("loan", new LoanDto());
        return "loan-issue";
    }

    @PostMapping("/issue")
    public String loanIssue(@Valid @ModelAttribute("loan") LoanDto loanDto,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal LibraryUserDetails currentUser,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "loan-issue";
        }
        try {
            Loan createdLoan = loanService.loanIssue(loanDto, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Книга успешно выдана! Номер операции: " + createdLoan.getId() +
                            ", срок возврата: " + createdLoan.getDueDate());
            return "redirect:/librarian";

        } catch (EntityNotFoundException | IllegalStateException e) {
            model.addAttribute("globalError", e.getMessage());
            return "loan-issue";

        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Произошла непредвиденная ошибка.");
            return "loan-issue";
        }
    }

    @GetMapping("/return")
    public String loanReturn(Model model) {
        model.addAttribute("loan", new LoanReturnDto());
        return "loan-return";
    }

    @PostMapping("/return")
    public String loanReturnPage(@Valid @ModelAttribute("loan") LoanReturnDto loanReturnDto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal LibraryUserDetails currentUser,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "loan-return";
        }
        try {
            Long days = loanService.loanReturn(loanReturnDto, currentUser.getId());
            if (days > 0) {
                redirectAttributes.addFlashAttribute("warningMessage",
                        "Книга успешно возвращена, но с просрочкой в " + days + " дн.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Книга успешно возвращена в срок!");
            }
            return "redirect:/librarian";

        } catch (EntityNotFoundException | IllegalStateException e) {
            model.addAttribute("globalError", e.getMessage());
            return "loan-return";

        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Произошла непредвиденная ошибка.");
            return "loan-return";
        }
    }

    @GetMapping("/catalog")
    public String showCatalogPage(@AuthenticationPrincipal LibraryUserDetails principal, Model model) {
        model.addAttribute("books", bookRepository.findAll());
        if (principal != null) {
            model.addAttribute("username", principal.getUser().getFirstName());
        }
        return "catalog-page";
    }

    @PostMapping("/book-reservation")
    public String bookReservation(@RequestParam("bookId") Long bookId,
                                  @AuthenticationPrincipal LibraryUserDetails currentUser,
                                  RedirectAttributes redirectAttributes) {
        try {
            reservationService.bookReservation(bookId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Книга успешно забронирована!");
            return "redirect:/reader/my-reservations";

        } catch (EntityNotFoundException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("globalError", e.getMessage());
            return "redirect:/catalog";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("globalError", "Произошла ошибка при бронировании.");
            return "redirect:/catalog";
        }
    }

    @GetMapping("/reader/my-reservations")
    public String myReservations(@AuthenticationPrincipal LibraryUserDetails currentUser, Model model) {
        model.addAttribute("reservations",
                reservationRepository.findByReaderId(currentUser.getId()));
        return "my-reservations";
    }

    @GetMapping("/reader/my-loans")
    public String myLoans(@AuthenticationPrincipal LibraryUserDetails currentUser, Model model) {
        model.addAttribute("loans",
                loanRepository.findByReaderIdAndReturnDateIsNullOrderByDueDateAsc(currentUser.getId()));
        model.addAttribute("today", java.time.LocalDate.now());
        return "my-loans";
    }

    @GetMapping("/reservation-issue")
    public String reservationIssuePage() {
        return "reservation-issue";
    }

    @GetMapping("/reservation-issue/search")
    public String searchReaderBookings(@RequestParam("cardNumber") String cardNumber, Model model) {
        String card = cardNumber.trim();
        if (card.isEmpty()) {
            model.addAttribute("globalError", "Введите номер читательского билета");
            return "reservation-issue";
        }
        List<Reservation> activeBookings = reservationRepository
                .findByReaderLibraryCardNumberAndStatus(card, ReservationStatus.PENDING);
        model.addAttribute("bookings", activeBookings);
        model.addAttribute("cardNumber", card);
        if (activeBookings.isEmpty()) {
            model.addAttribute("infoMessage", "У данного читателя нет активных броней, готовых к выдаче.");
        }
        return "reservation-issue";
    }

    @PostMapping("/reservation-issue")
    public String reservationIssue(@RequestParam("bookingId") Long bookingId,
                                   @AuthenticationPrincipal LibraryUserDetails currentUser,
                                   RedirectAttributes redirectAttributes) {
        try {
            Loan loan = reservationService.reservationIssue(bookingId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Книга успешно выдана! Номер операции: " + loan.getId() +
                            ", срок возврата: " + loan.getDueDate());
            return "redirect:/reservation-issue";

        } catch (EntityNotFoundException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("globalError", e.getMessage());
            return "redirect:/reservation-issue";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("globalError", "Не удалось выдать книгу по брони.");
            return "redirect:/reservation-issue";
        }
    }
}
