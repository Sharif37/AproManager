package com.example.demo.cardAttachment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardAttachmentRepository extends JpaRepository<card_attachment, Long> {
    // You can add custom queries if needed
}
