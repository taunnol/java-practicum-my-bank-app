package ru.yandex.practicum.bank.notifications.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.bank.common.dto.NotificationEvent;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @KafkaListener(
            topics = "${bank.kafka.topic.notifications}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onNotification(NotificationEvent event, Acknowledgment acknowledgment) {
        log.info("Notification: type={} amount={} actor={} target={} at={}",
                event.type(), event.amount(), event.actorLogin(),
                event.targetLogin(), event.occurredAt());
        acknowledgment.acknowledge();
    }
}
