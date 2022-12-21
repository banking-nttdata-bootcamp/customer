package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Customer;
import com.nttdata.bootcamp.repository.CustomerRepository;
import com.nttdata.bootcamp.service.CustomerService;
import com.nttdata.bootcamp.service.KafkaService;
import com.nttdata.bootcamp.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

//Service implementation
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Override
    public Flux<Customer> findAll() {
        Flux<Customer> customers = customerRepository.findAll();
        return customers;
    }

    @Override
    public Mono<Customer> findByDni(String dni) {
        Mono<Customer> customer = Mono.empty();
        Customer _customer = redisCacheService.retrieveCustomer(dni);

        //If the customer object not available in the cache DB, then need to retrieve it from the Mongo DB.
        if(_customer == null) {

            customer = customerRepository
                    .findAll()
                    .filter(x -> x.getDni().equals(dni))
                    .next();

            assert Objects.requireNonNull(customer.block()).getDni() != null;
            _customer = customer.block();

        }

        /*Mono<Customer> customer = customerRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni))
                .next();*/
        return customer;
    }

    @Override
    public Mono<Customer> save(Customer dataCustomer) {
        Mono<Customer> customerMono = findByDni(dataCustomer.getDni())
                .flatMap(__ -> Mono.<Customer>error(new Error("The customer with DNI" + dataCustomer.getDni() + " exists")))
                .switchIfEmpty(saveCustomer(dataCustomer));
        return customerMono;
    }

    @Override
    public Mono<Customer> updateAddress(Customer dataCustomer) {
        Mono<Customer> customerMono = findByDni(dataCustomer.getDni());
                //.delayElement(Duration.ofMillis(1000));
        try {
            Customer customer = customerMono.block();
            assert customer != null;
            customer.setAddress(dataCustomer.getAddress());
            customer.setModificationDate(dataCustomer.getModificationDate());
            return customerRepository.save(dataCustomer);
        }catch (Exception e){
            return Mono.<Customer>error(new Error("The customer with DNI " + dataCustomer.getDni() + " do not exists"));
        }
    }

    @Override
    public Mono<Customer> updateStatus(Customer dataCustomer) {
        Mono<Customer> customerMono = findByDni(dataCustomer.getDni());
        //.delayElement(Duration.ofMillis(1000));
        try {
            Customer customer = customerMono.block();
            assert customer != null;
            customer.setStatus(dataCustomer.getStatus());
            customer.setModificationDate(dataCustomer.getModificationDate());
            return customerRepository.save(customer);
        }catch (Exception e){
            return Mono.<Customer>error(new Error("The customer with DNI " + dataCustomer.getDni() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> delete(String dni) {
        Mono<Customer> customerMono = findByDni(dni);
        //customerMono.subscribe();
        try {
            return customerRepository.delete(customerMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("The customer with DNI" + dni + " do not exists"));
        }
    }

    public Mono<Customer> saveCustomer(Customer dataCustomer){
        Mono<Customer> monoCustomer = customerRepository.save(dataCustomer);
        redisCacheService.storeCustomer(monoCustomer.block().getDni(), monoCustomer.block());
        kafkaService.publish(monoCustomer.block());
        return monoCustomer;
    }

}
