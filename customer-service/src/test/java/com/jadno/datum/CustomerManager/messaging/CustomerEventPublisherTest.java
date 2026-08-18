package com.jadno.datum.CustomerManager.messaging;

import com.jadno.datum.CustomerManager.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(classes = CustomerEventPublisherTest.TestConfig.class)
class CustomerEventPublisherTest {

    private static final String TEST_QUEUE = "customer.created.publisher.test";

    @Autowired
    private CustomerEventPublisher publisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    void purgeQueue() {
        rabbitAdmin.initialize();
        rabbitAdmin.purgeQueue(TEST_QUEUE, true);
    }

    @Test
    @DisplayName("Should publish a customer.created event to RabbitMQ")
    void shouldPublishCustomerCreatedEvent() throws Exception {
        publisher.publishCustomerCreatedEvent("63276284006");

        Message message = rabbitTemplate.receive(TEST_QUEUE, 5_000);
        assertNotNull(message);

        CustomerEvent event = jsonMapper.readValue(message.getBody(), CustomerEvent.class);
        assertEquals("63276284006", event.cpf());
        assertNotNull(event.occurredAt());
        assertEquals(
                RabbitMQConstants.ROUTING_KEY_CUSTOMER_CREATED,
                message.getMessageProperties().getReceivedRoutingKey()
        );
    }

    @Configuration
    @Import({RabbitMQConfig.class, CustomerEventPublisher.class})
    static class TestConfig {

        @Bean
        ConnectionFactory connectionFactory() {
            CachingConnectionFactory factory = new CachingConnectionFactory();
            factory.setHost(System.getProperty("rabbitmq.host", "localhost"));
            factory.setPort(Integer.getInteger("rabbitmq.port", 5672));
            factory.setUsername("guest");
            factory.setPassword("guest");
            return factory;
        }

        @Bean
        RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
            return new RabbitAdmin(connectionFactory);
        }

        @Bean
        RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            template.setMessageConverter(messageConverter);
            return template;
        }

        @Bean
        Queue publisherTestQueue() {
            return new Queue(TEST_QUEUE, false, false, true);
        }

        @Bean
        Binding publisherTestBinding(Queue publisherTestQueue, TopicExchange customerEventsExchange) {
            return BindingBuilder.bind(publisherTestQueue)
                    .to(customerEventsExchange)
                    .with(RabbitMQConstants.ROUTING_KEY_CUSTOMER_CREATED);
        }

        @Bean
        JsonMapper jsonMapper() {
            return JsonMapper.builder().findAndAddModules().build();
        }
    }
}
