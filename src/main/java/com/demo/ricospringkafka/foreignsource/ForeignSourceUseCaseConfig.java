package com.demo.ricospringkafka.foreignsource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile("foreign")
public class ForeignSourceUseCaseConfig {}

@Profile("foreign")
@RestController
@Slf4j
class ForeignController{
	@Autowired
	private StreamBridge streamBridge;
	
	@PostMapping("/{destination}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void handler(@RequestBody final TestDomain body , @PathVariable("destination") final String destination) {
		log.info("body is {} and destination is {}", body, destination);
		
		
		Message<TestDomain> message = MessageBuilder.withPayload(body).build();
		
		boolean received = streamBridge.send("toStream-out-0", message);
		if(!received) throw new RuntimeException("not received");
		
		log.info("complete");
		
	}
}
