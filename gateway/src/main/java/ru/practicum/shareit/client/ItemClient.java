package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl + "/items");
    }

    public Object createItem(Long userId, Object itemDto) {
        return post("", userId, itemDto, null);
    }

    public Object updateItem(Long userId, Long itemId, Object itemDto) {
        return patch("/" + itemId, userId, itemDto, null);
    }

    public Object getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId, Collections.emptyMap());
    }

    public Object getAllItems(Long userId) {
        return get("", userId, null);
    }

    public Object searchItems(Long userId, String text) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search", userId, params);
    }
}