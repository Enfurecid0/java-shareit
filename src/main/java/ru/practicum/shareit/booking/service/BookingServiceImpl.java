package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InternalServerErrorException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.parse(bookingRequestDto.getStart()));
        booking.setEnd(LocalDateTime.parse(bookingRequestDto.getEnd()));

        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(booking.getStart())) {
            throw new IllegalArgumentException("Invalid dates");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Optional<Booking> optionalBooking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId);
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Booking not found or you are not the owner");
        }

        Booking booking = optionalBooking.get();
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findByIdAndBookerId(bookingId, userId);
        if (optionalBooking.isEmpty()) {
            optionalBooking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId);
        }
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Booking not found or you are not authorized");
        }
        return BookingMapper.toBookingDto(optionalBooking.get());
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, String state) {
        log.info("Getting bookings for user ID {} with state {}", userId, state);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new InternalServerErrorException("User not found"); // 500
                });
        LocalDateTime now = LocalDateTime.now();

        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown state: {}", state);
            throw new IllegalArgumentException("Unknown state: " + state); // 400
        }

        List<Booking> bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByBooker(user, now);
            case PAST -> bookingRepository.findPastByBooker(user, now);
            case FUTURE -> bookingRepository.findFutureByBooker(user, now);
            case WAITING -> bookingRepository.findWaitingByBooker(user);
            case REJECTED -> bookingRepository.findRejectedByBooker(user);
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId, String state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User not found")); // 500
        LocalDateTime now = LocalDateTime.now();

        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state); // 400
        }

        List<Booking> bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByOwner(owner, now);
            case PAST -> bookingRepository.findPastByOwner(owner, now);
            case FUTURE -> bookingRepository.findFutureByOwner(owner, now);
            case WAITING -> bookingRepository.findWaitingByOwner(owner);
            case REJECTED -> bookingRepository.findRejectedByOwner(owner);
            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        };

        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }
}