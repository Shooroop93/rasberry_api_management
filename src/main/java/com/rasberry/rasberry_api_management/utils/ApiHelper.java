package com.rasberry.rasberry_api_management.utils;

import com.rasberry.rasberry_api_management.dto.DtoTelegramMessageRequest;
import com.rasberry.rasberry_api_management.dto.EditMessageRequest;
import com.rasberry.rasberry_api_management.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiHelper {

    private final WebClient webClient;

    public String sendMessageTelegram(String message, String idChannel, String token, String messageId) {

        DtoTelegramMessageRequest body = null;
        URI uri = null;

        if (Objects.isNull(messageId)) {
            body = new SendMessageRequest(idChannel, message);

            uri = URI.create("https://api.telegram.org/bot" + token + "/sendMessage");

        } else {
            body = new EditMessageRequest(idChannel, messageId, message);

            uri = URI.create("https://api.telegram.org/bot" + token + "/editMessageText");

        }

        return sendMessageTelegram(uri, body);
    }

    private String sendMessageTelegram(URI uri, DtoTelegramMessageRequest body) {
        try {
            return webClient.post()
                    .uri(uri)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getResponseBodyAsString().contains("message is not modified")) {
                return "";
            }

            log.error("Telegram error {} {}\n. RequestBody: {}. \nResponseBody: {}",
                    e.getRawStatusCode(),
                    e.getStatusText(),
                    body,
                    e.getResponseBodyAsString());
            throw e;
        }
    }
}
