package com.example.demo.cardAttachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/card_attachments")
public class CardAttachmentController {

    private final CardAttachmentService cardAttachmentService;

    @Autowired
    public CardAttachmentController(CardAttachmentService cardAttachmentService) {
        this.cardAttachmentService = cardAttachmentService;
    }

    @GetMapping
    public List<card_attachment> getAllCardAttachments() {
        return cardAttachmentService.getAllCardAttachments();
    }

    @GetMapping("/{id}")
    public Optional<card_attachment> getCardAttachmentById(@PathVariable Long id) {
        return cardAttachmentService.getCardAttachmentById(id);
    }

    @PostMapping
    public card_attachment saveCardAttachment(@RequestBody card_attachment attachment) {
        return cardAttachmentService.saveCardAttachment(attachment);
    }

    @DeleteMapping("/{id}")
    public void deleteCardAttachment(@PathVariable Long id) {
        cardAttachmentService.deleteCardAttachment(id);
    }
}
