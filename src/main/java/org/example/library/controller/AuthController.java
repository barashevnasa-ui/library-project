package org.example.library.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.library.dto.LoginDto;
import org.example.library.security.LibraryUserDetails;
import org.example.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @GetMapping("/")
    public String homePage() {
        return "redirect:/catalog";
    }

    @GetMapping("/librarian")
    public String librarianPanel(@AuthenticationPrincipal LibraryUserDetails principal, Model model) {
        model.addAttribute("librarianName", principal.getUser().getFirstName());
        return "librarian";
    }

    @GetMapping("/deleteAccount")
    public String deletePage() {
        return "delete-account";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(@AuthenticationPrincipal LibraryUserDetails principal,
                                Authentication authentication,
                                HttpServletRequest req,
                                HttpServletResponse resp) {
        authService.deleteAccount(principal.getUser());
        new SecurityContextLogoutHandler().logout(req, resp, authentication);
        return "redirect:/login";
    }
}
