package com.example.demo.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardsService {

    private final CardsRepository cardsRepository;

    @Autowired
    public CardsService(CardsRepository cardsRepository) {
        this.cardsRepository = cardsRepository;
    }

    public List<Cards> getAllCards() {
        return cardsRepository.findAll();
    }

    public Cards getCardById(long cardId) {
        return cardsRepository.findById(cardId).orElse(null);
    }

    public Cards createCard(Cards card) {
        return cardsRepository.save(card);
    }

    public Cards updateCard(long cardId, Cards updatedCard) {
        Cards existingCard = cardsRepository.findById(cardId).orElse(null);
        if (existingCard != null) {
            // Update existingCard properties based on updatedCard
            // You may want to use model mappers or manually copy properties here
            existingCard.setCard_name(updatedCard.getCard_name());
            existingCard.setCard_description(updatedCard.getCard_description());
            existingCard.setCreate_date(updatedCard.getCreate_date());
            existingCard.setDue_date(updatedCard.getDue_date());
            existingCard.setReminder_date(updatedCard.getReminder_date());
            existingCard.setActive(updatedCard.isActive());

            return cardsRepository.save(existingCard);
        }
        return null;
    }

    public void deleteCard(long cardId) {
        cardsRepository.deleteById(cardId);
    }
}

