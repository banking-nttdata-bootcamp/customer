package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Customer;
import com.nttdata.bootcamp.entity.dto.ProductDto;
import com.nttdata.bootcamp.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bootcamp.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/report")
public class ReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
    @Autowired
    private CustomerService customerService;

    @GetMapping("/findAllProductsByCustomer/{dni}")
    public Flux<ProductDto> findAllProductsByCustomer(@PathVariable("dni") String dni) {
        Mono<Customer> customer= customerService.findByDni(dni);

        ArrayList<ProductDto> products = new ArrayList<ProductDto>();
        products.add(new ProductDto(dni,customer.block().getTypeCustomer(),"47522","staff",100.00));
        products.add(new ProductDto(dni,customer.block().getTypeCustomer(),"47523","saving",1580.00));
        products.add(new ProductDto(dni,customer.block().getTypeCustomer(),"47524","fixedTerm",25000.00));

        LOGGER.info("Registered productos by customer : " +dni+" :" + products);
        return Flux.fromStream(products.stream());
    }
}
