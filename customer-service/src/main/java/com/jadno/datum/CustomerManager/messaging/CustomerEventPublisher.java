package com.jadno.datum.CustomerManager.messaging;

import com.jadno.datum.CustomerManager.messaging.event.CustomerCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public CustomerEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCustomerCreated(String cpf) {
        CustomerCreatedEvent event = CustomerCreatedEvent.of(cpf);

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.CUSTOMER_EVENTS_EXCHANGE,
                RabbitMQConstants.ROUTING_KEY_CUSTOMER_CREATED,
                event
        );

        log.info("Published customer.created event for CPF {}", cpf);
    }
}