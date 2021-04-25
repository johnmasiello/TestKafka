package com.example.testKafka;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.example.testKafka.components.KafkaConsumer;
import com.example.testKafka.components.KafkaProducer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.ClassRule;
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
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@RunWith(SpringRunner.class)
@Import(KafkaTestContainersLiveTest.KafkaTestContainersConfiguration.class)
@SpringBootTest(classes = TestKafkaApplication.class)
@DirtiesContext
public class KafkaTestContainersLiveTest {

  @Configuration
  public static class KafkaTestContainersConfiguration {

    private static final Logger CONSUMER_CONFIG_LOGGER = LoggerFactory.getLogger("ConsumerConfig");
    private static final Logger PRODUCER_CONFIG_LOGGER = LoggerFactory.getLogger("ProducerConfig");

    @Bean
    public Map<String, Object> consumerConfigs(
        @Value("${spring.kafka.consumer.group-id}") String groupId) {
      Map<String, Object> props = new HashMap<>();
      String testContainersBootStrapServers = kafka.getBootstrapServers();
      CONSUMER_CONFIG_LOGGER
          .debug("bootstrap servers found as: {}", testContainersBootStrapServers);
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, testContainersBootStrapServers);
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
      // more standard configuration
      return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
      Map<String, Object> configProps = new HashMap<>();
      String testContainersBootStrapServers = kafka.getBootstrapServers();
      PRODUCER_CONFIG_LOGGER
          .debug("bootstrap servers found as: {}", testContainersBootStrapServers);
      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, testContainersBootStrapServers);
      // more standard configuration
      return new DefaultKafkaProducerFactory<>(configProps);
    }
  }

  @ClassRule
  public static KafkaContainer kafka =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

  @Autowired
  private KafkaConsumer consumer;

  @Autowired
  private KafkaProducer producer;

  @Value("${test.topic}")
  private String topic;

  @Test
  public void givenKafkaDockerContainer_whenSendingtoSimpleProducer_thenMessageReceived()
      throws Exception {
    producer.send(topic, "Sending with own controller");
    consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);

    assertThat(consumer.getLatch().getCount(), equalTo(0L));
    assertThat(consumer.getPayload(), containsString("embedded-test-topic"));
  }
}
