package org.example.library.service;

//@author Aliaksandra_Baranava

import lombok.RequiredArgsConstructor;
import org.example.library.dto.RegistrationReaderDto;
import org.example.library.model.*;
import org.example.library.repository.CodeRepository;
import org.example.library.repository.ReaderRepository;
import org.example.library.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Year;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ReaderRepository readerRepository;
    private final CodeRepository codeRepository;
    private final JavaMailSender mailSender;

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(normalize(email));
    }

    @Transactional
    public Reader createReader(RegistrationReaderDto dto) {
        String email = normalize(dto.getEmail());

        Reader reader = new Reader(email, dto.getFirstname().trim(), dto.getLastname().trim(), dto.getPhonenumber());
        reader.setRole(Role.READER);
        reader.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        reader.setStatus(false);
        reader.setLibraryCardNumber("LIB-" + Year.now().getValue() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        readerRepository.save(reader);

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        codeRepository.save(new VerificationCode(code, reader, CodeType.REGISTRATION, 10));
        sendEmail(email, code);

        return reader;
    }

    public void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Код подтверждения регистрации");
        message.setText("Здравствуйте! Ваш код для активации аккаунта в Библиотеке: " + code +
                "\nКод действителен в течение 10 минут.");
        mailSender.send(message);
    }

    @Transactional(noRollbackFor = IllegalStateException.class)
    public void verifyCode(String codeText, String email) {
        VerificationCode token = codeRepository.findByUserEmailAndType(normalize(email), CodeType.REGISTRATION)
                .orElseThrow(() -> new IllegalArgumentException("Код не найден или не отправлялся."));

        if (token.isExpired()) {
            codeRepository.delete(token);
            throw new IllegalStateException("Срок действия кода истёк. Запросите новый код.");
        }
        if (!token.getCode().equals(codeText == null ? "" : codeText.trim())) {
            throw new IllegalArgumentException("Неверный код подтверждения.");
        }

        User user = token.getUser();
        user.setStatus(true);
        userRepository.save(user);
        codeRepository.delete(token);
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
