package com.rasberry.rasberry_api_management.dto;

public record EditMessageRequest(String chat_id, String message_id, String text) implements DtoTelegramMessageRequest {
}