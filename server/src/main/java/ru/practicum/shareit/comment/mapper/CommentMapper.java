package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentRespondDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static CommentRespondDto toRespondDto(Comment comment) {
        return CommentRespondDto.builder()
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .id(comment.getId())
                .text(comment.getText())
                .build();
    }
}