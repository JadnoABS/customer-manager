package com.jadno.datum.CustomerManager.config;

import tools.jackson.databind.json.JsonMapper;
import com.jadno.datum.CustomerManager.messaging.RabbitMQConstants;
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
    public MessageConverter jsonMessageConverter(JsonMapper objectMapper) {
        return new JacksonJsonMessageConverter(objectMapper);
    }
}
