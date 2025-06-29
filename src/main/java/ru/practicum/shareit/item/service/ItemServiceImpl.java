package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        Item item = ItemMapper.toItem(itemDto, userStorage.getUser(userId));
        if ((item.getName() == null || item.getName().isBlank())
                || (item.getDescription() == null || item.getDescription().isBlank())) {
            throw new IllegalArgumentException("Поля name и description не могут быть пустыми");
        }
        return ItemMapper.toItemDto(itemStorage.createItem(userId, item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemStorage.getItem(userId, itemId);
        if (existingItem == null) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать предмет может только его владелец");
        }
        return ItemMapper.toItemDto(
                itemStorage.updateItem(userId, itemId, ItemMapper.toItem(itemDto, userStorage.getUser(userId))));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemStorage.getItem(userId, itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemStorage.getItem(userId, itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Удалить предмет может только его владелец");
        }
        itemStorage.deleteItem(userId, itemId);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return itemStorage.getAllItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        return itemStorage.searchItems(userId, text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}