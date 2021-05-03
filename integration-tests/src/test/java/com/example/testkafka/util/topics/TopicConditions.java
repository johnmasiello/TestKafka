package com.example.testkafka.util.topics;

import com.example.testkafka.models.TopicMessage;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicConditions {

  private static final Logger LOG = LoggerFactory.getLogger(TopicConditions.class);

  public static Optional<TopicMessage> fromRecord(ConsumerRecord<String, String> record) {
    return Optional.ofNullable(record.value())
        .map(value -> {
          LOG.info("Message received = {}", value);
          try {
            return ((TopicMessage) TopicSerializer.objectReader().readValue(value));

          } catch (IOException e) {
            LOG.error("Could not parse consumer record value", e);
            return null;
          }
        });
  }

  public static boolean topicStillContainsMessageWithID123(long millis) {
    return KafkaUtil.topicContainsMessageThat(Duration.ofMillis(millis),
        (ConsumerRecord<String, String> consumerRecord) ->
            fromRecord(consumerRecord)
                .map(message -> "123".equals(message.getId()))
                .orElse(false));
  }

  public static boolean topicNoLongerContainsMessageWithID123(long millis) {
    return KafkaUtil.topicDoesNotContainMessageThat(Duration.ofMillis(millis),
        (ConsumerRecord<String, String> consumerRecord) ->
            fromRecord(consumerRecord)
                .map(message -> "123".equals(message.getId()))
                .orElse(false));
  }
}
