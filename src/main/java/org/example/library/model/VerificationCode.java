package org.example.library.model;

//@author Aliaksandra_Baranava

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
@Getter
@Setter
@NoArgsConstructor
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeType type;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public VerificationCode(String code, User user, CodeType type, int durationInMinutes) {
        this.code = code;
        this.user = user;
        this.type = type;
        this.expiryDate = LocalDateTime.now().plusMinutes(durationInMinutes);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}

