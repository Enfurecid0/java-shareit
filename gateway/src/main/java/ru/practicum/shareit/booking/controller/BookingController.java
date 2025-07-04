package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

@Slf4j
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        Object response = bookingClient.createBooking(userId, bookingRequestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{bookingId}")
    public Object approveBooking(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Object getBooking(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public Object getBookingsByUser(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public Object getBookingsByOwner(
            @RequestHeader(HEADER_USER_ID) Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookingsByOwner(ownerId, state);
    }
}