package ru.yandex.practicum.bank.cash.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.bank.cash.api.dto.CashAction;
import ru.yandex.practicum.bank.cash.client.AccountsClient;
import ru.yandex.practicum.bank.cash.client.NotificationsClient;

import java.time.OffsetDateTime;

@Service
public class CashService {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;

    public CashService(AccountsClient accountsClient, NotificationsClient notificationsClient) {
        this.accountsClient = accountsClient;
        this.notificationsClient = notificationsClient;
    }

    public void apply(String login, long value, CashAction action) {
        if (value <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0");
        }

        if (action == CashAction.PUT) {
            accountsClient.deposit(login, value);
            notificationsClient.send(new NotificationEvent("CASH_IN", value, login, null, OffsetDateTime.now()));
        } else {
            accountsClient.withdraw(login, value);
            notificationsClient.send(new NotificationEvent("CASH_OUT", value, login, null, OffsetDateTime.now()));
        }
    }
}