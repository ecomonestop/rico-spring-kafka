package com.demo.ricospringkafka.foreignsource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.demo.ricospringkafka.RicoSpringKafkaApplication;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

class ForeignSourceUseCaseConfigTest {

	@Test
	void testSuccessfulPost() throws JsonParseException, JsonMappingException, IOException {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestChannelBinderConfiguration.getCompleteConfiguration(RicoSpringKafkaApplication.class))
						.run("--spring.profiles.active=foreign")) {
			WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();
			OutputDestination target = context.getBean(OutputDestination.class);

			TestDomain expected = new TestDomain(100l, "test");

			client.post().uri("/test").body(Mono.just(expected), TestDomain.class).exchange().expectStatus()
					.isAccepted().expectBody().isEmpty();

			Message<byte[]> outputMessage = target.receive(0, "toStream");
			ObjectMapper objectMapper = new ObjectMapper();
			TestDomain actual = objectMapper.readValue(outputMessage.getPayload(), TestDomain.class);
			

			assertThat(actual).isEqualTo(expected);

		}
	}

}
