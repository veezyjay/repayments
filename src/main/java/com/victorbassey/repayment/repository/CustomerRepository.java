package com.victorbassey.repayment.repository;

import com.victorbassey.repayment.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByCustomerNameContainingIgnoreCase(String value);
}
