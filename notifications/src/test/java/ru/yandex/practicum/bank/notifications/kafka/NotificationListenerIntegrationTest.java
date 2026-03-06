package ru.yandex.practicum.bank.notifications.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.yandex.practicum.bank.common.dto.NotificationEvent;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "bank.notifications")
class NotificationListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @MockitoSpyBean
    private NotificationListener notificationListener;

    @Value("${bank.kafka.topic.notifications}")
    private String topic;

    @Test
    void shouldReceiveNotificationEvent() {
        NotificationEvent event = new NotificationEvent(
                "CASH_IN", 500, "user1", null, OffsetDateTime.now());

        kafkaTemplate.send(topic, event.actorLogin(), event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(notificationListener).onNotification(any(NotificationEvent.class), any()));
    }
}
