package ru.practicum.shareit.itemRequest.mapper;

import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .build();
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}