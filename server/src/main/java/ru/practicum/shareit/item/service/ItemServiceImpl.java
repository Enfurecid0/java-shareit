package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.mapper.CommentMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank");
        }
        Item item = ItemMapper.toItem(itemDto, owner);
        item.setCreated(LocalDateTime.now());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = optionalItem.get();
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can update the item");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            throw new NotFoundException("Item not found");
        }
        if (!item.getOwner().getId().equals(userId)) {
            boolean isBooker = item.getBookings().stream()
                    .anyMatch(booking -> booking.getBooker().getId().equals(userId));
            if (!isBooker) {
                throw new NotFoundException("You are not authorized to view this item");
            }
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item != null && item.getOwner().getId().equals(userId)) {
            itemRepository.deleteById(itemId);
        } else {
            throw new NotFoundException("Item not found or you are not authorized");
        }
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return itemRepository.findByOwnerIdOrderByCreatedDesc(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .filter(item -> !item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemOwnerDto getByIdAndOwnerId(Long id, Long userId) {

        Item item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item with id - %d not found"
                        .formatted(id))
        );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastBookingDate = bookingRepository.findLastBooking(item.getId(), now)
                .map(Booking::getEnd)
                .orElse(null);

        LocalDateTime nextBookingDate = bookingRepository.findNextBooking(item.getId(), now)
                .map(Booking::getStart)
                .orElse(null);

        ItemOwnerDto itemOwnerDto = ItemMapper.toItemOwnerDto(item, lastBookingDate, nextBookingDate);
        itemOwnerDto.setComments(commentRepository.findByItemId(item.getId())
                .stream().map(CommentMapper::toRespondDto).toList());
        return itemOwnerDto;
    }
}