package event.customer;


import com.example.commonservice.dto.Money;

public class CustomerCreatedEvent implements CustomerEvent {
  private String name;
  private Money creditLimit;

  public CustomerCreatedEvent() {
  }

  public CustomerCreatedEvent(String name, Money creditLimit) {
    this.name = name;
    this.creditLimit = creditLimit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Money getCreditLimit() {
    return creditLimit;
  }

  public void setCreditLimit(Money creditLimit) {
    this.creditLimit = creditLimit;
  }
}
