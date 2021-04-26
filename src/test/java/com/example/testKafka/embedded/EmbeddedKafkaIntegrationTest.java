package com.example.testKafka.embedded;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.example.testKafka.components.KafkaConsumer;
import com.example.testKafka.components.KafkaProducer;
import io.cucumber.spring.CucumberContextConfiguration;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@CucumberContextConfiguration
@EmbeddedKafka(partitions = 1,
		brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Ignore
public class EmbeddedKafkaIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedKafkaIntegrationTest.class);

		@Autowired
    KafkaConsumer consumer;

    @Autowired
    KafkaProducer producer;

    @Value("${test.topic}")
    String topic;

  @Before
  public void setUp() {
    LOGGER.info("-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
  }

  @Test
  public void testConsumerConsumes() throws InterruptedException{
    producer.send(topic, "Sending with own simple KafkaProducer");
    consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
    assertThat(consumer.getLatch().getCount(), equalTo(0L));
    assertThat(consumer.getPayload(), containsString("embedded-test-topic"));
  }
}
