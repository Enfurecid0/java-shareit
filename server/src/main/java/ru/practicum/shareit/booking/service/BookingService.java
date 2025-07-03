package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto approveBooking(Long userId, Long bookingId, boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingsByUser(Long userId, String state);

    List<BookingDto> getBookingsByOwner(Long userId, String state);
}