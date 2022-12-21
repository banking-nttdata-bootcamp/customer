package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Customer;

/**
 * @author Ruben.M
 *
 */
public interface RedisCacheService {

	Customer storeCustomer(String customerId, Customer customer);

	Customer retrieveCustomer(String customerId);

	void flushCustomerCache(String customerId);

	void clearAll();

}
