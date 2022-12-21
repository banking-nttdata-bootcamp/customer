package com.nttdata.bootcamp.service.impl;

import com.google.gson.Gson;
import com.nttdata.bootcamp.entity.Customer;
import com.nttdata.bootcamp.service.RedisCacheService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @author Ruben.M
 *
 */
@Service
public class RedisCacheServiceImpl implements RedisCacheService {

	@Autowired
	private JedisPool jedisPool;

	private final Gson gson = new Gson();

	//TTL(Time to live) of session data in seconds 
	@Value("${redis.sessiondata.ttl}")
	private int sessiondataTTL;

	private final Logger logger = LogManager.getLogger(RedisCacheServiceImpl.class);

	// Acquire Jedis instance from the jedis pool.
	private Jedis acquireJedisInstance() {

		return jedisPool.getResource();
	}

	// Releasing the current Jedis instance once completed the job.
	private void releaseJedisInstance(Jedis jedis) {

		if (jedis != null) {
			jedis.close();
			jedis = null;
		}
	}

	@Override
	public Customer storeCustomer(String customerDni, Customer customer) {

		Jedis jedis = null;

		try {

			jedis = acquireJedisInstance();

			String json = gson.toJson(customer);
			jedis.set(customerDni, json);
			jedis.expire(customerDni, sessiondataTTL);

		} catch (Exception e) {
			logger.error("Error occured while storing data to the cache ", e.getMessage());
			releaseJedisInstance(jedis);
			throw new RuntimeException(e);

		} finally {
			releaseJedisInstance(jedis);
		}

		return customer;
	}

	@Override
	public Customer retrieveCustomer(String customerDni) {

		Jedis jedis = null;

		try {

			jedis = acquireJedisInstance();

			String customerJson = jedis.get(customerDni);

			if (StringUtils.hasText(customerJson)) {
				return gson.fromJson(customerJson, Customer.class);
			}

		} catch (Exception e) {
			logger.error("Error occured while retrieving data from the cache ", e.getMessage());
			releaseJedisInstance(jedis);
			throw new RuntimeException(e);

		} finally {
			releaseJedisInstance(jedis);
		}

		return null;
	}

	@Override
	public void flushCustomerCache(String customerId) {

		Jedis jedis = null;
		try {

			jedis = acquireJedisInstance();

			List<String> keys = jedis.lrange(customerId, 0, -1);
			if (!CollectionUtils.isEmpty(keys)) {
				// add the list key in as well
				keys.add(customerId);

				// delete the keys and list
				jedis.del(keys.toArray(new String[keys.size()]));
			}
		} catch (Exception e) {
			logger.error("Error occured while flushing specific data from the cache ", e.getMessage());
			releaseJedisInstance(jedis);
			throw new RuntimeException(e);

		} finally {
			releaseJedisInstance(jedis);
		}

	}

	@Override
	public void clearAll() {

		Jedis jedis = null;
		try {

			jedis = acquireJedisInstance();
			jedis.flushAll();

		} catch (Exception e) {
			logger.error("Error occured while flushing all data from the cache ", e.getMessage());
			releaseJedisInstance(jedis);
			throw new RuntimeException(e);

		} finally {
			releaseJedisInstance(jedis);
		}

	}

}
