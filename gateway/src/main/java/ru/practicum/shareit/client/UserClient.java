package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserClient extends BaseClient {

    public UserClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl + "/users");
    }

    public Object createUser(Object userDto) {
        return post("", 0L, userDto, Collections.emptyMap());
    }

    public Object getUser(Long userId) {
        return get("/" + userId, null, Collections.emptyMap());
    }

    public Object updateUser(Long userId, Object userDto) {
        return patch("/" + userId, null, userDto, null);
    }

    public void deleteUser(Long userId) {
        webClient.delete()
                .uri("/" + userId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}