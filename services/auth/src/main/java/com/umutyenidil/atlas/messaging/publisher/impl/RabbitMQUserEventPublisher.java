package com.umutyenidil.atlas.messaging.publisher.impl;

import com.umutyenidil.atlas.dto.event.UserRegisteredEvent;
import com.umutyenidil.atlas.messaging.publisher.UserEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQUserEventPublisher implements UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQUserEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${atlas.rabbitmq.exchange.user}")
    private String userExchangeName;

    @Value("${atlas.rabbitmq.routing-key.user-registered}")
    private String userRegisteredRoutingKey;

    @Override
    public void publishUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            rabbitTemplate.convertAndSend(userExchangeName, userRegisteredRoutingKey, event);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
