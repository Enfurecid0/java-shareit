package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommentClient extends BaseClient {

    public CommentClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl + "/items");
    }

    public Object addComment(Long userId, Long itemId, Object commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto, null);
    }
}