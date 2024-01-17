package com.example.demo.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardsController {

    private final CardsService cardsService;

    @Autowired
    public CardsController(CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @GetMapping
    public List<Cards> getAllCards() {
        return cardsService.getAllCards();
    }

    @GetMapping("/{cardId}")
    public Cards getCardById(@PathVariable long cardId) {
        return cardsService.getCardById(cardId);
    }

    @PostMapping
    public Cards createCard(@RequestBody Cards card) {
        return cardsService.createCard(card);
    }

    @PutMapping("/{cardId}")
    public Cards updateCard(@PathVariable long cardId, @RequestBody Cards card) {
        return cardsService.updateCard(cardId, card);
    }

    @DeleteMapping("/{cardId}")
    public void deleteCard(@PathVariable long cardId) {
        cardsService.deleteCard(cardId);
    }
}
