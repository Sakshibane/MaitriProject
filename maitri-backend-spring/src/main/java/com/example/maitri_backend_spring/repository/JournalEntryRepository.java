package com.example.maitri_backend_spring.repository;

// CORRECTED: Ensure this import has the underscore
import com.example.maitri_backend_spring.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    // By extending JpaRepository, we get methods like save(), findById(), findAll(), delete()
    // for free. We don't need to write any code here for basic operations.
}