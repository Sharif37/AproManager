package com.example.demo.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
public class ListsController {

    private final ListsService listsService;

    @Autowired
    public ListsController(ListsService listsService) {
        this.listsService = listsService;
    }

    @GetMapping
    public List<Lists> getAllLists() {
        return listsService.getAllLists();
    }

    @GetMapping("/{listId}")
    public Lists getListById(@PathVariable Long listId) {
        return listsService.getListById(listId);
    }

    @PostMapping
    public Lists createList(@RequestBody Lists list) {
        return listsService.createList(list);
    }

    @PutMapping("/{listId}")
    public Lists updateList(@PathVariable Long listId, @RequestBody Lists list) {
        return listsService.updateList(listId, list);
    }

    @DeleteMapping("/{listId}")
    public void deleteList(@PathVariable Long listId) {
        listsService.deleteList(listId);
    }
}

