package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;
    private final UserStorage userStorage;

    @Override
    public Item createItem(Long userId, Item item) {
        item.setId(id);
        items.put(item.getId(), item);
        id++;
        return item;
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item updatedItem = items.get(itemId);
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, updatedItem);
        return updatedItem;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String textToLowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(textToLowerCase)
                        || item.getName().toLowerCase().contains(textToLowerCase))
                        && item.getAvailable().booleanValue())
                .toList();
    }
}