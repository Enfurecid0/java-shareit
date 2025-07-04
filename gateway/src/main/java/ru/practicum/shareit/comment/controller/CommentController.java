package ru.practicum.shareit.comment.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.CommentClient;

@RestController
@RequestMapping("/items")
public class CommentController {

    private final CommentClient commentClient;

    public CommentController(CommentClient commentClient) {
        this.commentClient = commentClient;
    }

    @PostMapping("/{itemId}/comment")
    public Object addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @RequestBody Object commentDto) {
        return commentClient.addComment(userId, itemId, commentDto);
    }
}