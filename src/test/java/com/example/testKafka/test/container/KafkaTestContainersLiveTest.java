package com.example.testKafka.test.container;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.example.testKafka.TestKafkaApplication;
import com.example.testKafka.components.KafkaConsumer;
import com.example.testKafka.components.KafkaProducer;
import io.cucumber.spring.CucumberContextConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
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
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@CucumberContextConfiguration
@Import(KafkaTestContainersLiveTest.KafkaTestContainersConfiguration.class)
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
      configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
      configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
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
      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
      // more standard configuration
      return new DefaultKafkaProducerFactory<>(configProps);
    }
  }

  @ClassRule
  static KafkaContainer kafka;

  static {
    kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
    kafka.start();
  }

  @Autowired
  KafkaConsumer consumer;

  @Autowired
  KafkaProducer producer;

  @Value("${test.topic}")
  String topic;

  @Test
  public void testConsumerConsumes() throws InterruptedException {
    producer.send(topic, "Sending with own simple KafkaProducer");
    consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
    assertThat(consumer.getLatch().getCount(), equalTo(0L));
    assertThat(consumer.getPayload(), containsString("embedded-test-topic"));
  }
}
