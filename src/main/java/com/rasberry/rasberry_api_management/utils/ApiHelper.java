package com.rasberry.rasberry_api_management.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiHelper {

    public static void sendMessegeTelegram(String message, String token) {
        try {
            String body = String.format(
                    "{\"chat_id\": \"474173326\", \"text\": \"%s\"}",
                    message.replace("\"", "\\\"")
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + token + "/sendMessage"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при отправке в Telegram", e);
        }
    }
}
