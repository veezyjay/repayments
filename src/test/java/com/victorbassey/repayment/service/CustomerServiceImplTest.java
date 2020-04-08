package com.victorbassey.repayment.service;

import com.victorbassey.repayment.model.Customer;
import com.victorbassey.repayment.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    @Test
    void getAllCustomers() {
        List<Customer> theCustomers = List.of(new Customer(), new Customer(), new Customer());
        when(customerRepository.findAll()).thenReturn(theCustomers);
        List<Customer> allCustomers = customerService.getAllCustomers();
        assertNotNull(allCustomers);
        assertEquals(3, allCustomers.size());
    }

    @Test
    void findCustomers() {
        when(customerRepository.findAllByCustomerNameContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        List<Customer> foundCustomers = customerService.findCustomers("Frank");
        assertNotNull(foundCustomers);
        assertEquals(0, foundCustomers.size());
    }
}