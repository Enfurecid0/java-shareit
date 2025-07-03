package ru.practicum.shareit.client;

import exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {

    public BookingClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl + "/bookings");
    }

    public Object createBooking(Long userId, Object bookingRequestDto) {
        try {
            log.info("Sending POST to /bookings with userId={}, body={}", userId, bookingRequestDto);
            Object response = post("", userId, bookingRequestDto, Collections.emptyMap());
            if (response == null) {
                throw new NotFoundException("Item not found");
            }
            return response;
        } catch (RuntimeException e) {
            log.error("Server error: {}", e.getMessage());
            throw e;
        }
    }

    public Object approveBooking(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId, userId, null, params);
    }

    public Object getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId, Collections.emptyMap());
    }

    public Object getBookingsByUser(Long userId, String state) {
        Map<String, Object> params = Map.of("state", state);
        return get("", userId, params);
    }

    public Object getBookingsByOwner(Long ownerId, String state) {
        try {
            log.info("Sending GET to /bookings/owner with ownerId={}, state={}", ownerId, state);
            Map<String, Object> params = Map.of("state", state);
            Object response = get("/owner", ownerId, params);
            if (response == null) {
                throw new NotFoundException("Bookings not found");
            }
            return response;
        } catch (RuntimeException e) {
            log.error("Server error: {}", e.getMessage());
            throw e;
        }
    }
}