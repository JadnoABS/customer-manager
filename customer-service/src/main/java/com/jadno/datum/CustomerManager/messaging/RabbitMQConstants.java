package com.jadno.datum.CustomerManager.messaging;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    public static final String CUSTOMER_EVENTS_EXCHANGE = "customer.events";
    public static final String ROUTING_KEY_CUSTOMER_CREATED = "customer.created";
}