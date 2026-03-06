package ru.yandex.practicum.bank.cash.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.bank.common.dto.NotificationEvent;

@Component
public class KafkaNotificationsClient implements NotificationsClient {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Value("${bank.kafka.topic.notifications}")
    private String topic;

    public KafkaNotificationsClient(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(NotificationEvent event) {
        kafkaTemplate.send(topic, event.actorLogin(), event);
    }
}
