package com.jadno.datum.ScoreService.messaging;

import com.jadno.datum.ScoreService.domain.ScoreService;
import tools.jackson.databind.json.JsonMapper;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Instant;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CustomerCreatedListenerTest.TestConfig.class })
public class CustomerCreatedListenerTest {

    @ClassRule
    public static DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("docker-compose.yml"))
                    .withExposedService("rabbitmq", 5672, Wait.forListeningPort());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ScoreService scoreService;

    @Test
    public void shouldReceivesMessageAndCallsServiceWhenCustomerCreatedEvent() {
        String cpf = "12345678900";
        CustomerEvent event = new CustomerEvent(cpf, Instant.now());

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.CUSTOMER_EVENTS_EXCHANGE,
                RabbitMQConstants.ROUTING_KEY_CUSTOMER_CREATED,
                event
        );

        verify(scoreService, timeout(5000).times(1)).createScore(cpf);
    }

    @Configuration
    @EnableRabbit
    @Import({RabbitMQConfig.class, CustomerCreatedListener.class})
    static class TestConfig {

        @Bean
        public ConnectionFactory connectionFactory() {
            CachingConnectionFactory factory = new CachingConnectionFactory();
            factory.setHost(environment.getServiceHost("rabbitmq", 5672));
            factory.setPort(environment.getServicePort("rabbitmq", 5672));
            factory.setUsername("guest");
            factory.setPassword("guest");
            return factory;
        }

        @Bean
        public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
            RabbitAdmin admin = new RabbitAdmin(connectionFactory);
            admin.setAutoStartup(true);
            return admin;
        }

        @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            template.setMessageConverter(messageConverter);
            return template;
        }

        @Bean
        public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
                ConnectionFactory connectionFactory, MessageConverter messageConverter) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            factory.setMessageConverter(messageConverter);
            return factory;
        }

        @Bean
        public ScoreService scoreService() {
            return Mockito.mock(ScoreService.class);
        }

        @Bean
        public JsonMapper jsonMapper() {
            return JsonMapper.builder().findAndAddModules().build();
        }
    }
}