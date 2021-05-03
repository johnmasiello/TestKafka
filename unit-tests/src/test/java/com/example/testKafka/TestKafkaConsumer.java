package com.example.testKafka;

import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class TestKafkaConsumer implements MessageListener<Object, Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestKafkaConsumer.class);

  private CountDownLatch latch = new CountDownLatch(1);

  private String payload = null;

  @Override
  @KafkaListener(topics = "${test.topic}", groupId = "${test.group-id}", autoStartup = "false")
  public void onMessage(ConsumerRecord<Object, Object> consumerRecord) {
    LOGGER.info("Test Consumer - received payload='{}'", consumerRecord.toString());
    setPayload(consumerRecord.toString());
    latch.countDown();
  }

  public CountDownLatch getLatch() {
    return latch;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }
}
