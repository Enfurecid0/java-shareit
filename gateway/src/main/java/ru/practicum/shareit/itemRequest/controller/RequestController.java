package ru.practicum.shareit.itemRequest.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.RequestClient;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestClient requestClient;

    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public Object createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestBody Object requestDto) {
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public Object getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public Object getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size) {
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public Object getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}