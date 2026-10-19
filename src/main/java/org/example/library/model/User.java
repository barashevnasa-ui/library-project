package org.example.library.model;

//@author Aliaksandra_Baranava

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // Reader и Librarian хранятся в своих таблицах, id общий с users
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String passwordHash;
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdDate;
    private boolean status;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    public User(String email, String firstname, String lastname, String phonenumber) {
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastname;
        this.phoneNumber = phonenumber;
    }
}
