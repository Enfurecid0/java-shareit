package ru.practicum.shareit.itemRequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}