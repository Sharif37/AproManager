package com.example.demo.list;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ListsRepository extends JpaRepository<Lists, Long> {
    // Add custom queries if needed
}

