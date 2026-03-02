package ru.yandex.practicum.bank.cash.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.bank.cash.service.NotificationEvent;

@Component
@ConditionalOnProperty(name = "bank.security.enabled", havingValue = "false", matchIfMissing = true)
public class NoopNotificationsClient implements NotificationsClient {
    @Override public void send(NotificationEvent event) { }
}