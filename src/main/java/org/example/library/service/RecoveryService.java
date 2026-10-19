package org.example.library.service;

//@author Aliaksandra_Baranava

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.library.model.CodeType;
import org.example.library.model.User;
import org.example.library.model.VerificationCode;
import org.example.library.repository.CodeRepository;
import org.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RecoveryService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    @Transactional
    public void sendResetCode(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с такой почтой не найден."));
        codeRepository.findByUserEmailAndType(email, CodeType.PASSWORD_RESET).ifPresent(codeRepository::delete);
        String code = String.format("%06d", new Random().nextInt(999999));
        VerificationCode resetCode = new VerificationCode(code, user, CodeType.PASSWORD_RESET, 10);
        codeRepository.save(resetCode);
        sendEmail(email, code);
    }

    @Transactional
    public void resetPassword(String email, String codeText, String newPassword) {
        VerificationCode token = codeRepository.findByUserEmailAndType(email, CodeType.PASSWORD_RESET)
                .orElseThrow(() -> new IllegalArgumentException("Запрос на сброс пароля не найден."));

        if (!token.getCode().equals(codeText)) {
            throw new IllegalArgumentException("Неверный код подтверждения.");
        }
        if (token.isExpired()) {
            codeRepository.delete(token);
            throw new IllegalStateException("Срок действия кода истек. Запросите код заново.");
        }
        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        codeRepository.delete(token);
    }

    private void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Восстановление пароля в Библиотеке");
        message.setText("Здравствуйте! Ваш код для сброса пароля: " + code +
                "\nЕсли вы не запрашивали сброс, просто проигнорируйте это письмо.");
        mailSender.send(message);
    }

}
