package ru.gpb.app.dto;

import java.util.UUID;

public record AccountResponse(UUID accountId, String accountName) {
}
