package com.victorbassey.repayment.controller.v1;

import com.victorbassey.repayment.model.Customer;
import com.victorbassey.repayment.payload.ResponseTemplate;
import com.victorbassey.repayment.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Validated
public class CustomerController {
    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseTemplate<List<Customer>> getAllCustomers() {
        List<Customer> allCustomers = customerService.getAllCustomers();
        return new ResponseTemplate<>(HttpStatus.OK, "Successfully retrieved all customers", allCustomers);
    }

    @GetMapping("/filter")
    public ResponseTemplate<List<Customer>> findCustomers(@RequestParam String value) {
        List<Customer> theCustomers = customerService.findCustomers(value);
        return new ResponseTemplate<>(HttpStatus.OK, "Customers successfully searched", theCustomers);
    }
}
