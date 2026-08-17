package com.jadno.datum.CustomerManager.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CustomerEventPublisher customerEventPublisher;

    private final String CPF = "98765432100";

    @Test
    void shouldShouldSendToRabbitWithCorrectRouting() {
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        customerEventPublisher.publishCustomerCreatedEvent(CPF);

        String expectedExchange = "customer.events";
        String expectedRoutingKey = "customer.created";

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(expectedExchange),
                eq(expectedRoutingKey),
                eventCaptor.capture()
        );

        assertNotNull(eventCaptor.getValue());
    }
}