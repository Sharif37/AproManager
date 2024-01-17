package com.example.demo.cardAttachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CardAttachmentService {

    private final CardAttachmentRepository cardAttachmentRepository;

    @Autowired
    public CardAttachmentService(CardAttachmentRepository cardAttachmentRepository) {
        this.cardAttachmentRepository = cardAttachmentRepository;
    }

    public List<card_attachment> getAllCardAttachments() {
        return cardAttachmentRepository.findAll();
    }

    public Optional<card_attachment> getCardAttachmentById(Long id) {
        return cardAttachmentRepository.findById(id);
    }

    public card_attachment saveCardAttachment(card_attachment attachment) {
        return cardAttachmentRepository.save(attachment);
    }

    public void deleteCardAttachment(Long id) {
        cardAttachmentRepository.deleteById(id);
    }
}

