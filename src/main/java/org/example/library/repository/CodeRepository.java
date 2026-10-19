package org.example.library.repository;

//@author Aliaksandra_Baranava

import org.example.library.model.CodeType;
import org.example.library.model.VerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeRepository extends CrudRepository<VerificationCode, String> {
    Optional<VerificationCode> findByUserEmailAndType(String email, CodeType type);
}
