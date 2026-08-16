package com.jadno.datum.ScoreService.config;

import tools.jackson.databind.json.JsonMapper;
import com.jadno.datum.ScoreService.messaging.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange customerEventsExchange() {
        return new TopicExchange(RabbitMQConstants.CUSTOMER_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue customerCreatedQueue() {
        return new Queue(RabbitMQConstants.QUEUE_CUSTOMER_CREATED, true);
    }

    @Bean
    public Binding customerCreatedBinding(Queue customerCreatedQueue, TopicExchange customerEventsExchange) {
        return BindingBuilder
                .bind(customerCreatedQueue)
                .to(customerEventsExchange)
                .with(RabbitMQConstants.ROUTING_KEY_CUSTOMER_CREATED);
    }

    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper objectMapper) {
        return new JacksonJsonMessageConverter(objectMapper);
    }
}