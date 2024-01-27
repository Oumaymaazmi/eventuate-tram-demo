package event.customer;

public class CustomerCreditReleasedEvent extends AbstractCustomerOrderEvent {

  public CustomerCreditReleasedEvent() {
  }

  public CustomerCreditReleasedEvent(Long orderId) {
    super(orderId);
  }
}
