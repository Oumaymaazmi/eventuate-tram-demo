package com.example.orderservice.domain;

import enumeration.OrderState;
import com.example.orderservice.exceptions.PendingOrderCantBeCancelledException;
import event.order.OrderCreatedEvent;
import event.order.OrderDetails;
import io.eventuate.tram.events.publisher.ResultWithEvents;

import javax.persistence.*;

import static java.util.Collections.singletonList;

@Entity
@Table(name="orders")
@Access(AccessType.FIELD)
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private OrderState state;

  @Embedded
  private OrderDetails orderDetails;

  @Version
  private Long version;

  public Order() {
  }

  public Order(OrderDetails orderDetails) {
    this.orderDetails = orderDetails;
    this.state = OrderState.PENDING;
  }

  public static ResultWithEvents<Order> createOrder(OrderDetails orderDetails) {
    Order order = new Order(orderDetails);
    OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(orderDetails);
    return new ResultWithEvents<>(order, singletonList(orderCreatedEvent));
  }

  public Long getId() {
    return id;
  }

  public void noteCreditReserved() {
    this.state = OrderState.APPROVED;
  }

  public void noteCreditReservationFailed() {
    this.state = OrderState.REJECTED;
  }

  public OrderState getState() {
    return state;
  }

  public OrderDetails getOrderDetails() {
    return orderDetails;
  }

  public void cancel() {
    switch (state) {
      case PENDING:
        throw new PendingOrderCantBeCancelledException();
      case APPROVED:
        this.state = OrderState.CANCELLED;
        return;
      default:
        throw new UnsupportedOperationException("Can't cancel in this state: " + state);
    }
  }
}
