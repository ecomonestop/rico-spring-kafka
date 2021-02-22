package com.demo.ricospringkafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

class SimpleExampleConfigTest {

	@Test
	void test() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestChannelBinderConfiguration.getCompleteConfiguration(RicoSpringKafkaApplication.class))
						.run("--spring.profiles.active=simple")) {
			InputDestination source = context.getBean(InputDestination.class);
			OutputDestination target = context.getBean(OutputDestination.class);

			Message<byte[]> inputMessage = MessageBuilder.withPayload("hello".getBytes()).build();
			source.send(inputMessage, "lowercase");


			Message<byte[]> outputMessage = target.receive(0, "uppercase");
			assertThat(outputMessage.getPayload()).isEqualTo("HELLO".getBytes());

		}
	}
}
