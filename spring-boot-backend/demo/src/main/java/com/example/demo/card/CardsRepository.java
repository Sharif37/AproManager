package com.example.demo.card;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardsRepository extends JpaRepository<Cards, Long> {
    // Add custom queries if needed
}

