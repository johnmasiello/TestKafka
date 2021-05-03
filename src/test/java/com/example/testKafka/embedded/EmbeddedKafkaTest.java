package com.example.testKafka.embedded;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import com.example.testKafka.TestKafkaConsumer;
import com.example.testKafka.components.KafkaProducer;
import com.example.testKafka.util.ContainerUtil;
import io.cucumber.spring.CucumberContextConfiguration;
import java.time.Duration;
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
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@CucumberContextConfiguration
@Import(TestKafkaConsumer.class)
@EmbeddedKafka(partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@Ignore
public class EmbeddedKafkaTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedKafkaTest.class);

  @Autowired
  TestKafkaConsumer testConsumer;

  @Autowired
  ConsumerFactory<Object, Object> consumerFactory;

  @Autowired
  KafkaProducer producer;

  @Value("${test.topic}")
  String topic;

  @Value("${test.group-id}")
  String groupId;

  @Before
  public void setUp() {
    LOGGER.info(
        "-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
  }

  @Test
  public void testConsumerConsumes() throws InterruptedException {
    Duration waitDuration = Duration.ofSeconds(5);
    // WHEN
    producer.send(topic, "Sending with own simple KafkaProducer");
    Thread.sleep(waitDuration.toMillis());
    ContainerUtil.addConsumerToContainer(testConsumer, consumerFactory, topic, groupId);
    testConsumer.getLatch().await(waitDuration.getSeconds(), TimeUnit.SECONDS);
    // THEN
    assertThat(testConsumer.getLatch().getCount(), equalTo(1L));
    assertThat(testConsumer.getPayload(), nullValue());
  }
}
