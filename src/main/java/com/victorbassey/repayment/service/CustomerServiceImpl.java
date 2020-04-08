package com.victorbassey.repayment.service;

import com.victorbassey.repayment.model.Customer;
import com.victorbassey.repayment.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> findCustomers(String value) {
        return customerRepository.findAllByCustomerNameContainingIgnoreCase(value);
    }
}
