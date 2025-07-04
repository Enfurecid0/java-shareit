package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    public RequestClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl + "/requests");
    }

    public Object createRequest(Long userId, Object requestDto) {
        return post("", userId, requestDto, null);
    }

    public Object getUserRequests(Long userId) {
        return get("", userId, null);
    }

    public Object getAllRequests(Long userId, int from, int size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("/all", userId, params);
    }

    public Object getRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId, Collections.emptyMap());
    }
}