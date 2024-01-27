package com.example.orderservice.service;

import event.customer.CustomerCreditReservationFailedEvent;
import event.customer.CustomerCreditReservedEvent;
import event.customer.CustomerValidationFailedEvent;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.springframework.beans.factory.annotation.Autowired;


public class CustomerEventConsumer {

    @Autowired
    private OrderService orderService;

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("io.eventuate.examples.tram.ordersandcustomers.customers.domain.Customer")
                .onEvent(CustomerCreditReservedEvent.class, this::handleCustomerCreditReservedEvent)
                .onEvent(CustomerCreditReservationFailedEvent.class, this::handleCustomerCreditReservationFailedEvent)
                .onEvent(CustomerValidationFailedEvent.class, this::handleCustomerValidationFailedEvent)
                .build();
    }

    private void handleCustomerCreditReservedEvent(DomainEventEnvelope<CustomerCreditReservedEvent> domainEventEnvelope) {
        orderService.approveOrder(domainEventEnvelope.getEvent().getOrderId());
    }

    private void handleCustomerCreditReservationFailedEvent(DomainEventEnvelope<CustomerCreditReservationFailedEvent> domainEventEnvelope) {
        orderService.rejectOrder(domainEventEnvelope.getEvent().getOrderId());
    }

    private void handleCustomerValidationFailedEvent(DomainEventEnvelope<CustomerValidationFailedEvent> domainEventEnvelope) {
        orderService.rejectOrder(domainEventEnvelope.getEvent().getOrderId());
    }

}
