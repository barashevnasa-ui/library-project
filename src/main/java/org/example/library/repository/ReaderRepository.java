package org.example.library.repository;

//@author Aliaksandra_Baranava

import org.example.library.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByLibraryCardNumber(String libraryCardNumber);
}
