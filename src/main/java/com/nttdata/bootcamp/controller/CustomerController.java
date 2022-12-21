package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.dto.BusinessCustomerDto;
import com.nttdata.bootcamp.entity.dto.UpdateAddressDto;
import com.nttdata.bootcamp.entity.dto.PersonalCustomerDto;
import com.nttdata.bootcamp.entity.dto.UpdateStatusDto;
import com.nttdata.bootcamp.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bootcamp.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/customer")
public class CustomerController {
	//test
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
	@Autowired
	private CustomerService customerService;

	//Customer search
	@GetMapping("/")
	public Flux<Customer> findAllCustomers() {
		Flux<Customer> customers = customerService.findAll();
		LOGGER.info("Registered Customers: " + customers);
		return customers;
	}

	//Search for clients by DNI
	@GetMapping("/findByClient/{dni}")
	public Mono<Customer> findByClientDNI(@PathVariable("dni") String dni) {
		LOGGER.info("Searching client by DNI: " + dni);
		return customerService.findByDni(dni);
	}

	//Save personal customer
	@PostMapping(value = "/savePersonalCustomer")
	public Mono<Customer> savePersonalCustomer(@RequestBody PersonalCustomerDto customer){

		Customer dataCustomer = new Customer();
		Mono.just(dataCustomer).doOnNext(t -> {
			t.setDni(customer.getDni());
			t.setTypeCustomer(Constant.PERSONAL_CUSTOMER);
			t.setFlagVip(false);
			t.setFlagPyme(false);
			t.setName(customer.getName());
			t.setSurName(customer.getSurName());
			t.setAddress(customer.getAddress());
			t.setStatus(Constant.CUSTOMER_ACTIVE);
			t.setCreationDate(new Date());
			t.setModificationDate(new Date());
		}).onErrorReturn(dataCustomer).onErrorResume(e -> Mono.just(dataCustomer))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Customer> newCustomer = customerService.save(dataCustomer);
		if(newCustomer != null){
			customerService.saveInitServices(newCustomer.block());
		}
		return newCustomer;
	}

	//Save business customer
	@PostMapping(value = "/saveBusinessCustomer")
	public Mono<Customer> saveBusinessCustomer(@RequestBody BusinessCustomerDto customer){

		Customer dataCustomer = new Customer();
		Mono.just(dataCustomer).doOnNext(t -> {
			t.setDni(customer.getDni());
			t.setTypeCustomer(Constant.BUSINESS_CUSTOMER);
			t.setFlagPyme(false);
			t.setFlagVip(false);
			t.setName(customer.getName());
			t.setSurName(customer.getSurName());
			t.setAddress(customer.getAddress());
			t.setStatus(Constant.CUSTOMER_ACTIVE);
			t.setCreationDate(new Date());
			t.setModificationDate(new Date());
		}).onErrorReturn(dataCustomer).onErrorResume(e -> Mono.just(dataCustomer))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Customer> newCustomer = customerService.save(dataCustomer);
		if(newCustomer != null){
			customerService.saveInitServices(newCustomer.block());
		}
		return newCustomer;
	}

	//Update address customer
	@PutMapping("/updateCustomerAddress/{dni}")
	public Mono<Customer> updateCustomerAddress(@PathVariable("dni") String dni,
										 @Valid @RequestBody UpdateAddressDto customer) {

		Customer dataCustomer = new Customer();
		Mono.just(dataCustomer).doOnNext(t -> {
			t.setDni(dni);
			t.setAddress(customer.getAddress());
			t.setModificationDate(new Date());
		}).onErrorReturn(dataCustomer).onErrorResume(e -> Mono.just(dataCustomer))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Customer> updateCustomer = customerService.updateAddress(dataCustomer);
		return updateCustomer;
	}

	//Update status customer
	@PutMapping("/updateCustomerStatus/{dni}")
	public Mono<Customer> updateCustomerStatus(@PathVariable("dni") String dni,
										 @Valid @RequestBody UpdateStatusDto customer) {

		Customer dataCustomer = new Customer();
		Mono.just(dataCustomer).doOnNext(t -> {
			t.setDni(dni);
			t.setStatus(customer.getStatus());
			t.setModificationDate(new Date());
		}).onErrorReturn(dataCustomer).onErrorResume(e -> Mono.just(dataCustomer))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Customer> updateCustomer = customerService.updateStatus(dataCustomer);
		return updateCustomer;
	}

	//Delete customer
	@DeleteMapping("/delete/{dni}")
	public ResponseEntity<Mono<Void>> deleteCustomer(@PathVariable("dni") String dni) {
		LOGGER.info("Deleting client by DNI: " + dni);
		Mono<Void> deleteCustomer = customerService.delete(dni);
		return ResponseEntity.noContent().build();
	}

}
