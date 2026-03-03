package ru.yandex.practicum.bank.notifications.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record NotificationRequest(
        @NotNull NotificationType type,
        @Positive long amount,
        @NotNull String actorLogin,
        String targetLogin,
        @NotNull OffsetDateTime occurredAt
) {
}
