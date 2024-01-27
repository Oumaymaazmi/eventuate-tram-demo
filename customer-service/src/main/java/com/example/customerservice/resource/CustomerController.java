package com.example.customerservice.resource;

import com.example.customerservice.domain.Customer;
import com.example.customerservice.dto.CreateCustomerRequest;
import com.example.customerservice.dto.CreateCustomerResponse;
import com.example.customerservice.service.CustomerService;
import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.tram.viewsupport.rebuild.DomainSnapshotExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private CustomerService customerService;

    private DomainSnapshotExportService<Customer> domainSnapshotExportService;

    @Autowired
    public CustomerController(CustomerService customerService,
                              DomainSnapshotExportService<Customer> domainSnapshotExportService) {
        this.customerService = customerService;
        this.domainSnapshotExportService = domainSnapshotExportService;
    }


    @RequestMapping(value = "/customers", method = RequestMethod.POST)
    public CreateCustomerResponse createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        Customer customer = customerService.createCustomer(createCustomerRequest.getName(), createCustomerRequest.getCreditLimit());
        return new CreateCustomerResponse(customer.getId());
    }

    @RequestMapping(value = "/customers/make-snapshot", method = RequestMethod.POST)
    public String makeSnapshot() {
        return JSonMapper.toJson(domainSnapshotExportService.exportSnapshots());
    }
}
