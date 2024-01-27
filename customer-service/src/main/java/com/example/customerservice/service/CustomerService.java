package com.example.customerservice.service;


import com.example.commonservice.dto.Money;
import com.example.customerservice.domain.Customer;
import com.example.customerservice.exceptions.CustomerCreditLimitExceededException;
import com.example.customerservice.repository.CustomerRepository;
import event.customer.CustomerCreditReservationFailedEvent;
import event.customer.CustomerCreditReservedEvent;
import event.customer.CustomerValidationFailedEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;

public class CustomerService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private CustomerRepository customerRepository;

  private DomainEventPublisher domainEventPublisher;

  public CustomerService(CustomerRepository customerRepository, DomainEventPublisher domainEventPublisher) {
    this.customerRepository = customerRepository;
    this.domainEventPublisher = domainEventPublisher;
  }

  @Transactional
  public Customer createCustomer(String name, Money creditLimit) {
    ResultWithEvents<Customer> customerWithEvents = Customer.create(name, creditLimit);
    Customer customer = customerRepository.save(customerWithEvents.result);
    domainEventPublisher.publish(Customer.class, customer.getId(), customerWithEvents.events);
    return customer;
  }

  void reserveCredit(long orderId, long customerId, Money orderTotal) {

    Optional<Customer> possibleCustomer = customerRepository.findById(customerId);

    if (!possibleCustomer.isPresent()) {
      logger.info("Non-existent customer: {}", customerId);
      domainEventPublisher.publish(Customer.class,
              customerId,
              Collections.singletonList(new CustomerValidationFailedEvent(orderId)));
      return;
    }

    Customer customer = possibleCustomer.get();


    try {
      customer.reserveCredit(orderId, orderTotal);

      CustomerCreditReservedEvent customerCreditReservedEvent =
              new CustomerCreditReservedEvent(orderId);

      domainEventPublisher.publish(Customer.class,
              customer.getId(),
              Collections.singletonList(customerCreditReservedEvent));

    } catch (CustomerCreditLimitExceededException e) {

      CustomerCreditReservationFailedEvent customerCreditReservationFailedEvent =
              new CustomerCreditReservationFailedEvent(orderId);

      domainEventPublisher.publish(Customer.class,
              customer.getId(),
              Collections.singletonList(customerCreditReservationFailedEvent));
    }
  }

  void releaseCredit(long orderId, long customerId) {
    Customer customer = customerRepository.findById(customerId).get();
    customer.unreserveCredit(orderId);
  }
}
