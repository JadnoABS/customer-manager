package com.jadno.datum.ScoreService.messaging;

import com.jadno.datum.ScoreService.domain.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedListener {

    @Autowired
    private ScoreService scoreService;

    private static final Logger log = LoggerFactory.getLogger(CustomerCreatedListener.class);

    @RabbitListener(queues = RabbitMQConstants.QUEUE_CUSTOMER_CREATED)
    public void onCustomerCreated(CustomerCreatedEvent event) {
        log.info("Evento customer.created recebido para CPF {}", event.cpf());
        scoreService.createScore(event.cpf());
    }
}