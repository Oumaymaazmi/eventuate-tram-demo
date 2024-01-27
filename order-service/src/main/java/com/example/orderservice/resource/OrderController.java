package com.example.orderservice.resource;

import com.example.orderservice.domain.Order;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.dto.GetOrderResponse;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import event.order.OrderDetails;
import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.tram.viewsupport.rebuild.DomainSnapshotExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private DomainSnapshotExportService<Order> domainSnapshotExportService;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderRepository orderRepository,
                           DomainSnapshotExportService<Order> domainSnapshotExportService) {

        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.domainSnapshotExportService = domainSnapshotExportService;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        Order order = orderService.createOrder(new OrderDetails(createOrderRequest.getCustomerId(), createOrderRequest.getOrderTotal()));
        return new CreateOrderResponse(order.getId());
    }

    @RequestMapping(value = "/orders/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<GetOrderResponse> getOrder(@PathVariable Long orderId) {
        return orderRepository
                .findById(orderId)
                .map(this::makeSuccessfulResponse)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/orders/{orderId}/cancel", method = RequestMethod.POST)
    public ResponseEntity<GetOrderResponse> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return makeSuccessfulResponse(order);
    }

    @RequestMapping(value = "/orders/make-snapshot", method = RequestMethod.POST)
    public String makeSnapshot() {
        return JSonMapper.toJson(domainSnapshotExportService.exportSnapshots());
    }

    private ResponseEntity<GetOrderResponse> makeSuccessfulResponse(Order order) {
        return new ResponseEntity<>(new GetOrderResponse(order.getId(), order.getState()), HttpStatus.OK);
    }
}
