package org.example.library.model;

//@author Aliaksandra_Baranava

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "readers")
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
@Getter
@Setter
@NoArgsConstructor
public class Reader extends User {

    @Column(unique = true, nullable = false)
    private String libraryCardNumber;

    public Reader(String email, String firstname, String lastname, String phonenumber) {
        super(email, firstname, lastname, phonenumber);
    }
}
