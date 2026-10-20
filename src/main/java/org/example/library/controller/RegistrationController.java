package org.example.library.controller;

import jakarta.validation.Valid;
import org.example.library.dto.RegistrationReaderDto;
import org.example.library.service.RecoveryService;
import org.example.library.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private RecoveryService recoveryService;

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new RegistrationReaderDto());
        return "registration";
    }

    @PostMapping("/registration")
    public String register(@Valid @ModelAttribute("user") RegistrationReaderDto registrationReaderDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        try {
            if (registrationService.isEmailTaken(registrationReaderDto.getEmail())) {
                bindingResult.rejectValue("email", "email.error", "Пользователь с такой почтой уже есть.");
                return "registration";
            }
            registrationService.createReader(registrationReaderDto);
            redirectAttributes.addFlashAttribute("email", registrationReaderDto.getEmail());
            return "redirect:/verify";

        } catch (IllegalStateException e) {
            model.addAttribute("globalError", e.getMessage());
            return "registration";
        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Не удалось завершить регистрацию. Попробуйте позже.");
            return "registration";
        }
    }

    @GetMapping("/verify")
    public String showVerifyPage() {
        return "verify-code-page";
    }

    @PostMapping("/verify")
    public String processVerification(@RequestParam("code") String code,
                                      @RequestParam("email") String email,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        try {
            registrationService.verifyCode(code, email);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Аккаунт успешно активирован! Теперь вы можете войти.");
            return "redirect:/login";

        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("globalError", e.getMessage());
            model.addAttribute("email", email);
            return "verify-code-page";
        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Не удалось подтвердить аккаунт.");
            model.addAttribute("email", email);
            return "verify-code-page";
        }
    }

    @GetMapping("/recovery")
    public String recoveryGet() {
        return "recover";
    }

    @PostMapping("/recovery")
    public String recoveryPost(@RequestParam("email") String email,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            recoveryService.sendResetCode(email);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/recovery/reset";   // было: return "recovery-reset"
        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Не удалось отправить код.");
            return "recover";
        }
    }

    @GetMapping("/recovery/reset")
    public String showResetPasswordPage(@ModelAttribute("email") String email, Model model) {
        if (email == null || email.isBlank()) {
            return "redirect:/recovery";
        }
        model.addAttribute("email", email);
        return "reset-password-page";
    }

    @PostMapping("/recovery/reset")
    public String processPasswordReset(@RequestParam("code") String code,
                                       @RequestParam("email") String email,
                                       @RequestParam("newPassword") String newPassword,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (!newPassword.matches(RegistrationReaderDto.PASSWORD_REGEX)) {
            model.addAttribute("globalError",
                    "Пароль: 8–20 символов, латинские буквы, цифры и хотя бы один спецсимвол.");
            model.addAttribute("email", email);
            return "reset-password-page";
        }
        try {
            recoveryService.resetPassword(email, code, newPassword);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Пароль успешно изменён! Войдите в систему, используя новые данные.");
            return "redirect:/login";

        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("globalError", e.getMessage());
            model.addAttribute("email", email);
            return "reset-password-page";
        } catch (RuntimeException e) {
            model.addAttribute("globalError", "Не удалось сменить пароль.");
            model.addAttribute("email", email);
            return "reset-password-page";
        }
    }
}
