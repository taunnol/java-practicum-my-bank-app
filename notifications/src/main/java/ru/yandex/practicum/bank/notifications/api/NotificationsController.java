package ru.yandex.practicum.bank.notifications.api;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.bank.notifications.api.dto.NotificationRequest;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    private static final Logger log = LoggerFactory.getLogger(NotificationsController.class);

    @PostMapping
    public void notify(@Valid @RequestBody NotificationRequest req) {
        log.info("Notification: type={} amount={} actor={} target={} at={}",
                req.type(), req.amount(), req.actorLogin(), req.targetLogin(), req.occurredAt());
    }
}