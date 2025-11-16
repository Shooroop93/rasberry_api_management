package com.rasberry.rasberry_api_management.dto;

public record SendMessageRequest(String chat_id, String text) implements DtoTelegramMessageRequest{
}
