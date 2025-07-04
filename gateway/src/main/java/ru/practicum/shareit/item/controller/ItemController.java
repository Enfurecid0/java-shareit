package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.client.ItemClient;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public Object createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public Object getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id) {
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public Object getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public Object searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestParam(required = false) String text) {
        return itemClient.searchItems(userId, text);
    }
}