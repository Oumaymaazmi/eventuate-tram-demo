package event.customer;

public class CustomerCreditReservedEvent extends AbstractCustomerOrderEvent {

  public CustomerCreditReservedEvent() {
  }

  public CustomerCreditReservedEvent(Long orderId) {
    super(orderId);
  }
}
