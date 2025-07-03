package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
public abstract class BaseClient {

    protected final WebClient webClient;

    public BaseClient(String serverUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public Object get(String path, Long userId, Map<String, ?> params) {
        try {
            return webClient.get()
                    .uri(path, params != null ? params : Collections.emptyMap())
                    .header("X-Sharer-User-Id", String.valueOf(userId))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Resource not found: {}", e.getMessage());
            return null;
        } catch (WebClientResponseException e) {
            log.error("Server error: {}", e.getMessage());
            throw new RuntimeException("Server error: " + e.getMessage());
        }
    }

    public Object post(String path, Long userId, Object body, Map<String, ?> params) {
        try {
            return webClient.post()
                    .uri(path, params != null ? params : Collections.emptyMap())
                    .header("X-Sharer-User-Id", String.valueOf(userId))
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Resource not found: {}", e.getMessage());
            return null;
        } catch (WebClientResponseException e) {
            log.error("Server error: {}", e.getMessage());
            throw new RuntimeException("Server error: " + e.getMessage());
        }
    }

    public Object patch(String path, Long userId, Object body, Map<String, ?> params) {
        try {
            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            if (params != null) {
                params.forEach((key, value) -> queryParams.add(key, value.toString()));
            }

            var requestSpec = webClient.patch()
                    .uri(uriBuilder -> uriBuilder.path(path)
                            .queryParams(queryParams)
                            .build())
                    .header("X-Sharer-User-Id", String.valueOf(userId));

            if (body != null) {
                requestSpec.bodyValue(body);
            }

            return requestSpec.retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Resource not found: {}", e.getMessage());
            return null;
        } catch (WebClientResponseException e) {
            log.error("Server error: {}", e.getMessage());
            throw new RuntimeException("Server error: " + e.getMessage());
        }
    }
}