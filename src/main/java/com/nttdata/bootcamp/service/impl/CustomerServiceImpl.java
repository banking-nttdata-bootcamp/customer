package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Customer;
import com.nttdata.bootcamp.repository.CustomerRepository;
import com.nttdata.bootcamp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Flux<Customer> findAll() {
        Flux<Customer> customers = customerRepository.findAll();
        return customers;
    }

    @Override
    public Mono<Customer> findByDni(String dni) {
        Mono<Customer> customer = customerRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni))
                .next();
        return customer;
    }

    @Override
    public Mono<Customer> save(Customer dataCustomer) {
        Mono<Customer> customerMono = findByDni(dataCustomer.getDni())
                .flatMap(__ -> Mono.<Customer>error(new Error("El cliente con dni " + dataCustomer.getDni() + " YA EXISTE")))
                .switchIfEmpty(customerRepository.save(dataCustomer));
        return customerMono;
    }

    @Override
    public Mono<Customer> updateAddress(Customer dataCustomer) {
        Mono<Customer> customerMono = findByDni(dataCustomer.getDni());
                //.delayElement(Duration.ofMillis(1000));
        try {
            Customer customer = customerMono.block();
            customer.setAddress(dataCustomer.getAddress());
            customer.setModificationDate(dataCustomer.getModificationDate());
            return customerRepository.save(customer);
        }catch (Exception e){
            return Mono.<Customer>error(new Error("El cliente con dni " + dataCustomer.getDni() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Customer> updateStatus(Customer dataCustomer) {
        Mono<Customer> customerMono = findByDni(dataCustomer.getDni());
        //.delayElement(Duration.ofMillis(1000));
        try {
            Customer customer = customerMono.block();
            customer.setStatus(dataCustomer.getStatus());
            customer.setModificationDate(dataCustomer.getModificationDate());
            return customerRepository.save(dataCustomer);
        }catch (Exception e){
            return Mono.<Customer>error(new Error("El cliente con dni " + dataCustomer.getDni() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Void> delete(String dni) {
        Mono<Customer> customerMono = findByDni(dni);
        //customerMono.subscribe();
        try {
            return customerRepository.delete(customerMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("El cliente con dni " + dni + " NO EXISTE"));
        }
    }

}
