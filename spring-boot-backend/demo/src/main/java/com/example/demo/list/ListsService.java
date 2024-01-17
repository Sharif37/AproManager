package com.example.demo.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListsService {

    private final ListsRepository listsRepository;

    @Autowired
    public ListsService(ListsRepository listsRepository) {
        this.listsRepository = listsRepository;
    }

    public List<Lists> getAllLists() {
        return listsRepository.findAll();
    }

    public Lists getListById(Long listId) {
        return listsRepository.findById(listId).orElse(null);
    }

    public Lists createList(Lists list) {
        return listsRepository.save(list);
    }

    public Lists updateList(Long listId, Lists updatedList) {
        Lists existingList = listsRepository.findById(listId).orElse(null);
        if (existingList != null) {
            existingList.setName(updatedList.getName());
            existingList.setPosition(updatedList.getPosition());
            existingList.setBoard(updatedList.getBoard());

            return listsRepository.save(existingList);
        }
        return null;
    }

    public void deleteList(Long listId) {
        listsRepository.deleteById(listId);
    }
}
