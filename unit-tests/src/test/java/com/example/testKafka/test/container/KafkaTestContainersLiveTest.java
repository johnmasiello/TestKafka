package com.example.testKafka.test.container;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import com.example.testKafka.TestKafkaApplication;
import com.example.testKafka.TestKafkaConsumer;
import com.example.testKafka.components.KafkaProducer;
import com.example.testKafka.util.ContainerUtil;
import io.cucumber.spring.CucumberContextConfiguration;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@RunWith(SpringRunner.class)
@CucumberContextConfiguration
@Import({ KafkaTestContainersLiveTest.KafkaTestContainersConfiguration.class, TestKafkaConsumer.class })
@SpringBootTest(classes = TestKafkaApplication.class)
@DirtiesContext
@Ignore
public class KafkaTestContainersLiveTest {

  @Configuration
  public static class KafkaTestContainersConfiguration {

    private static final Logger CONSUMER_CONFIG_LOGGER = LoggerFactory.getLogger("ConsumerConfig");
    private static final Logger PRODUCER_CONFIG_LOGGER = LoggerFactory.getLogger("ProducerConfig");

    @Bean
    public ConsumerFactory<Object, Object> consumerConfigs(
        @Value("${spring.kafka.consumer.group-id}") String groupId) {
      Map<String, Object> configProps = new HashMap<>();
      String testContainersBootStrapServers = kafka.getBootstrapServers();
      CONSUMER_CONFIG_LOGGER
          .debug("bootstrap servers found as: {}", testContainersBootStrapServers);
      configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, testContainersBootStrapServers);
      configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
      configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringDeserializer");
      configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringDeserializer");
      // more standard configuration
      return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
      Map<String, Object> configProps = new HashMap<>();
      String testContainersBootStrapServers = kafka.getBootstrapServers();
      PRODUCER_CONFIG_LOGGER
          .debug("bootstrap servers found as: {}", testContainersBootStrapServers);
      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, testContainersBootStrapServers);
      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer");
      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer");
      // more standard configuration
      return new DefaultKafkaProducerFactory<>(configProps);
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestContainersLiveTest.class);

  @ClassRule
  public static KafkaContainer kafka;

  static {
    kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
    kafka.start();
  }

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
