package ru.yandex.practicum.bank.transfer.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.bank.transfer.client.AccountsClient;
import ru.yandex.practicum.bank.transfer.client.NotificationsClient;

import java.time.OffsetDateTime;

@Service
public class TransferService {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;

    public TransferService(AccountsClient accountsClient, NotificationsClient notificationsClient) {
        this.accountsClient = accountsClient;
        this.notificationsClient = notificationsClient;
    }

    public void transfer(String fromLogin, String toLogin, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0");
        }
        if (toLogin == null || toLogin.isBlank()) {
            throw new IllegalArgumentException("Получатель обязателен");
        }
        if (fromLogin.equals(toLogin)) {
            throw new IllegalArgumentException("Нельзя переводить самому себе");
        }

        accountsClient.withdraw(fromLogin, amount);

        try {
            accountsClient.deposit(toLogin, amount);
        } catch (RuntimeException e) {
            try {
                accountsClient.deposit(fromLogin, amount);
            } catch (RuntimeException ignored) {
            }
            throw e;
        }

        notificationsClient.send(new NotificationEvent(
                "TRANSFER",
                amount,
                fromLogin,
                toLogin,
                OffsetDateTime.now()
        ));
    }
}