package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
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

    @Override
    public BookingDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        log.info("Processing booking creation with userId: {}, bookingRequestDto: {}", userId, bookingRequestDto);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new NotFoundException("User not found");
                });

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> {
                    log.warn("Item not found with id: {}", bookingRequestDto.getItemId());
                    return new NotFoundException("Item not found");
                });

        if (!item.getAvailable()) {
            log.warn("Item with id: {} is not available", item.getId());
            throw new IllegalArgumentException("Item is not available");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.parse(bookingRequestDto.getStart()));
        booking.setEnd(LocalDateTime.parse(bookingRequestDto.getEnd()));

        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(booking.getStart())) {
            log.warn("Invalid dates for booking: start={}, end={}", booking.getStart(), booking.getEnd());
            throw new IllegalArgumentException("Invalid dates");
        }

        log.info("Saving booking: {}", booking);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {
        log.info("Approving booking: bookingId={}, userId={}, approved={}", bookingId, userId, approved);
        Optional<Booking> optionalBooking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId);
        if (optionalBooking.isEmpty()) {
            log.error("Booking not found or user is not the owner: bookingId={}, userId={}", bookingId, userId);
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findCurrentByBooker(user, now);
            case "PAST" -> bookingRepository.findPastByBooker(user, now);
            case "FUTURE" -> bookingRepository.findFutureByBooker(user, now);
            case "WAITING" ->
                    bookingRepository.findByBookerAndStatus(user, BookingStatus.WAITING);
            case "REJECTED" ->
                    bookingRepository.findByBookerAndStatus(user, BookingStatus.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findCurrentByOwner(owner, now);
            case "PAST" -> bookingRepository.findPastByOwner(owner, now);
            case "FUTURE" -> bookingRepository.findFutureByOwner(owner, now);
            case "WAITING" ->
                    bookingRepository.findByOwnerAndStatus(owner, BookingStatus.WAITING);
            case "REJECTED" ->
                    bookingRepository.findByOwnerAndStatus(owner, BookingStatus.REJECTED);
            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}