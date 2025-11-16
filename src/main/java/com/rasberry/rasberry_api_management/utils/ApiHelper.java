package com.rasberry.rasberry_api_management.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Objects;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class ApiHelper {

    private final WebClient webClient;

    public String sendMessageTelegram(String message, String idChannel, String token, String messageId) {

        String body = null;
        URI uri = null;

        if (Objects.isNull(messageId)) {
            body = format("""
                        {"chat_id": "%s", "text": "%s"}
                    """, idChannel, message);

            uri = URI.create("https://api.telegram.org/bot" + token + "/sendMessage");

        } else {
            body = format("""
                        {"chat_id": "%s", "message_id": %d, "text": "%s"}
                    """, idChannel, Long.parseLong(messageId), message);

            uri = URI.create("https://api.telegram.org/bot" + token + "/editMessageText");

        }

        return sendPostMessage(uri, body);
    }

    private String sendPostMessage(URI uri, String body) {
        return webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
