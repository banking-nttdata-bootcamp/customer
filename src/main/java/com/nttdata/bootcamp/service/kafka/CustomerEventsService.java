package com.nttdata.bootcamp.service.kafka;

import com.nttdata.bootcamp.entity.Customer;
import com.nttdata.bootcamp.entity.enums.EventType;
import com.nttdata.bootcamp.events.CustomerCreatedEventKafka;
import com.nttdata.bootcamp.events.EventKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;

@Component
public class CustomerEventsService {
	
	@Autowired
	private KafkaTemplate<String, EventKafka<?>> producer;
	
	@Value("${topic.customer.name}")
	private String topicCustomer;
	
	public void publish(Customer customer) {

		CustomerCreatedEventKafka created = new CustomerCreatedEventKafka();
		created.setData(customer);
		created.setId(UUID.randomUUID().toString());
		created.setType(EventType.CREATED);
		created.setDate(new Date());

		this.producer.send(topicCustomer, created);
	}

}
