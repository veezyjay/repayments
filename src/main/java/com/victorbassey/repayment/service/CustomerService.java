package com.victorbassey.repayment.service;

import com.victorbassey.repayment.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers();
    List<Customer> findCustomers(String value);
}
