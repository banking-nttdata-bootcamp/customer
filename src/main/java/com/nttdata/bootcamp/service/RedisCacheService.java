package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Customer;

/**
 * @author Ruben.M
 *
 */
public interface RedisCacheService {

	Customer storeCustomer(String customerDni, Customer customer);

	Customer retrieveCustomer(String customerDni);

	void flushCustomerCache(String customerId);

	void clearAll();

}
