package com.umutyenidil.atlas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Value("${atlas.rabbitmq.queue.email}")
    private String emailQueueName;

    @Value("${atlas.rabbitmq.exchange.user}")
    private String userExchangeName;

    @Value("${atlas.rabbitmq.routing-key.user-registered}")
    private String userRegisteredRoutingKey;

    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueueName, true);
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(userExchangeName);
    }

    @Bean
    public Binding emailQueueBinding(Queue emailQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(userExchange)
                .with(userRegisteredRoutingKey);
    }

    @Bean
    @SuppressWarnings({"removal"})
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
