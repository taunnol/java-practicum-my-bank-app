package ru.yandex.practicum.bank.transfer.client;

import ru.yandex.practicum.bank.transfer.service.NotificationEvent;

public interface NotificationClient {
    void send(NotificationEvent event);
}