package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Customer;
import com.nttdata.bootcamp.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId("123456");
        customer.setDni("72384351");
        customer.setTypeCustomer("PERSONAL");
        customer.setFlagVip(true);
        customer.setFlagPyme(true);
        customer.setName("Ruben");
        customer.setSurName("Maza");
        customer.setAddress("Chiclayo");
        customer.setStatus("ACTIVE");
        customer.setCreationDate(new Date());
        customer.setModificationDate(new Date());
    }

    @Test
    void findAll() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));
    }
}