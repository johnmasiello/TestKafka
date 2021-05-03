package com.example.testKafka.util;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

public class ContainerUtil {

  // Based on https://bikas-katwal.medium.com/start-stop-kafka-consumers-or-subscribe-to-new-topic-programmatically-using-spring-kafka-2d4fb77c9117
  public static <K, V> void addConsumerToContainer(MessageListener<K, V> messageListener,
      ConsumerFactory<K, V> consumerFactory, String topic, String groupId) {
    assertNotNull(consumerFactory);
    ContainerProperties containerProps = new ContainerProperties(topic);

    containerProps.setGroupId(groupId);
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    containerProps.setKafkaConsumerProperties(properties);
    var container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
    container.setupMessageListener(messageListener);
    container.setConcurrency(1);
    container.start();
  }
}
