package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.booker = :user AND b.start <= :date AND b.end >= :date")
    List<Booking> findCurrentByBooker(@Param("user") User user, @Param("date") LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.booker = :user AND b.end < :date")
    List<Booking> findPastByBooker(@Param("user") User user, @Param("date") LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.booker = :user AND b.start > :date")
    List<Booking> findFutureByBooker(@Param("user") User user, @Param("date") LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.booker = :user AND b.status = 'WAITING'")
    List<Booking> findWaitingByBooker(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.booker = :user AND b.status = 'REJECTED'")
    List<Booking> findRejectedByBooker(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.start <= :date AND b.end >= :date")
    List<Booking> findCurrentByOwner(@Param("owner") User owner, @Param("date") LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.end < :date")
    List<Booking> findPastByOwner(@Param("owner") User owner, @Param("date") LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.start > :date")
    List<Booking> findFutureByOwner(@Param("owner") User owner, @Param("date") LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.status = 'WAITING'")
    List<Booking> findWaitingByOwner(@Param("owner") User owner);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.status = 'REJECTED'")
    List<Booking> findRejectedByOwner(@Param("owner") User owner);

    Optional<Booking> findByIdAndBookerId(Long bookingId, Long bookerId);

    Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.item.id = :itemId AND b.end < :now")
    List<Booking> findByBookerIdAndItemIdAndEndBefore(@Param("bookerId") Long bookerId, @Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findByItemOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= :now1 AND b.end >= :now2")
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(@Param("ownerId") Long ownerId, @Param("now1") LocalDateTime now1, @Param("now2") LocalDateTime now2);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :now")
    List<Booking> findByItemOwnerIdAndEndBefore(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :now")
    List<Booking> findByItemOwnerIdAndStartAfter(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> findByItemOwnerIdAndStatusContaining(@Param("ownerId") Long ownerId, @Param("status") String status);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "JOIN b.booker br " +
            "WHERE br.id = :userId AND " +
            "b.end < CURRENT_TIMESTAMP")
    boolean existsValidBookingForAddComment(@Param("userId") Long userId);

    boolean existsByItemId(Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item i " +
            "WHERE i.id = :itemId AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC " +
            "LIMIT 1")
    Booking getNearliestFutureBooking(@Param("itemId") Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item i " +
            "WHERE i.id = :itemId AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.end DESC " +
            "LIMIT 1")
    Booking getNearliestPastBooking(@Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.end < ?2 AND b.status = 'CANCELED' ORDER BY b.end DESC")
    Optional<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.start > ?2 ORDER BY b.start ASC")
    Optional<Booking> findNextBooking(Long itemId, LocalDateTime now);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);
}