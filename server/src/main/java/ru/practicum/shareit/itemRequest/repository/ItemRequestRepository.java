package ru.practicum.shareit.itemRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);

    List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(Long requesterId);
}