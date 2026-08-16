package com.jadno.datum.ScoreService.messaging;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    public static final String CUSTOMER_EVENTS_EXCHANGE = "customer.events";
    public static final String ROUTING_KEY_CUSTOMER_CREATED = "customer.created";
    public static final String QUEUE_CUSTOMER_CREATED = "score.customer.created.queue";
}