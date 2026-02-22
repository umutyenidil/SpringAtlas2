package com.umutyenidil.atlas.listener;

import com.umutyenidil.atlas.dto.event.UserRegisteredEvent;
import com.umutyenidil.atlas.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserEventListener {

    private final NotificationService notificationService;

    public UserEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${atlas.rabbitmq.queue.email}")
    public void handleUserRegistered(UserRegisteredEvent event) {
        notificationService.sendWelcomeEmail(event.email());
    }
}
