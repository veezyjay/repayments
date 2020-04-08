package com.victorbassey.repayment.controller.v1;

import com.victorbassey.repayment.model.Customer;
import com.victorbassey.repayment.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    CustomerService customerService;

    @InjectMocks
    CustomerController customerController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void getAllCustomers() throws Exception {
        List<Customer> theCustomers = List.of(new Customer(), new Customer(), new Customer());
        when(customerService.getAllCustomers()).thenReturn(theCustomers);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void findCustomers() throws Exception {
        when(customerService.findCustomers(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/customers/filter?value=mimi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customers successfully searched"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void returnErrorOnMissingRequestParam() throws Exception {
        mockMvc.perform(get("/api/v1/customers/filter"))
                .andExpect(status().isBadRequest());
    }
}