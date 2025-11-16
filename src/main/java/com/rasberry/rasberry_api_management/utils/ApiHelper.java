package com.rasberry.rasberry_api_management.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class ApiHelper {

    private final WebClient webClient;

    public String sendMessageTelegram(String message, String idChannel, String token, String messageId) {

        String body;

        if (Objects.isNull(messageId)) {
            body = format("""
                        {"chat_id": "%s", "text": "%s"}
                    """, idChannel, message);

            URI uri = URI.create("https://api.telegram.org/bot" + token + "/sendMessage");

            return webClient.post()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } else {
            body = format("""
                        {"chat_id": "%s", "message_id": "%s", "text": "%s"}
                    """, idChannel, messageId, message);

            URI uri = URI.create("https://api.telegram.org/bot" + token + "/editMessageText");

            return webClient.post()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
    }
}
