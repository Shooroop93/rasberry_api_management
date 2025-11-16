package com.rasberry.rasberry_api_management.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.lang.String.format;

public class ApiHelper {

    public static void sendMessageTelegram(String message, String idChannel, String token) {
        try {
            String body = format("""
                    {"chat_id": "%s", "text": "%s"}
                    """, idChannel, message);

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
