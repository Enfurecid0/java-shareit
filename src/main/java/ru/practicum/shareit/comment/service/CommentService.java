package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long itemId, String text);

    List<CommentDto> getItemComments(Long itemId);
}