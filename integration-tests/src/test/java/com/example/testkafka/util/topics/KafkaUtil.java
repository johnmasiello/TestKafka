package com.example.testkafka.util.topics;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaUtil {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaUtil.class);

  private static AtomicInteger clientIdCount = new AtomicInteger(1);

  public static void purgeAllMessagesInTopic(long milliseconds) {
    Consumer<String, String> consumer = createConsumer(TopicConstants.MAX_POLL_RECORDS_ON_PURGE);
    consume(consumer, Duration.ofMillis(milliseconds), consumerRecords ->
        ((ConsumerRecords<String, String>) consumerRecords).forEach(KafkaUtil::printRecord));
  }

  public static <K, V> void printRecord(ConsumerRecord<K, V> record) {
    LOG.info("consuming record - offset: {} - headers: {} - key: {} - value: {}",
        record.offset(), record.headers(), record.key(), record.value());
  }

  public static void publishPayload(Object payload) {
    Producer<String, String> producer = createProducer();
    LOG.info("Publishing payload: {}...", payload);

    try {
      String key = "test";
      ProducerRecord<String, String> record = new ProducerRecord<>(
          TopicConstants.TOPIC_NAME,
          key,
          TopicSerializer.objectWriter().writeValueAsString(payload));
      RecordMetadata metadata = producer.send(record).get();
      LOG.info("Record sent with key {} to partition {} with offset {}",
          key, metadata.partition(), metadata.offset());

    } catch (ExecutionException | InterruptedException e) {
      LOG.error("Error sending to topic " + TopicConstants.TOPIC_NAME, e);

    } catch (JsonProcessingException e) {
      LOG.error("Error formatting payload " + TopicConstants.TOPIC_NAME, e);
    }
  }

  /*
  https://dzone.com/articles/kafka-producer-and-consumer-example
   */
  public static Producer<String, String> createProducer() {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, TopicConstants.KAFKA_BROKERS);
    props.put(ProducerConfig.CLIENT_ID_CONFIG,
        TopicConstants.CLIENT_ID + clientIdCount.getAndIncrement());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, TopicConstants.MAX_BLOCK_MS_ON_SEND);
    props.put(ProducerConfig.RETRIES_CONFIG, TopicConstants.RETRIES_ON_SEND);
    //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
    return new KafkaProducer<>(props);
  }

  public static Consumer<String, String> createConsumer() {
    return createConsumer(TopicConstants.MAX_POLL_RECORDS);
  }

  public static Consumer<String, String> createConsumer(int maxPollRecords) {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TopicConstants.KAFKA_BROKERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, TopicConstants.GROUP_ID_CONFIG);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, TopicConstants.ENABLE_AUTO_COMMIT);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, TopicConstants.OFFSET_RESET_EARLIER);

    Consumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList(TopicConstants.TOPIC_NAME));
    return consumer;
  }

  public static void consume(Consumer consumer, Duration duration,
      java.util.function.Consumer<ConsumerRecords> onConsumerRecords) {

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    while (stopWatch.getTime() < duration.toMillis()) {
      onConsumerRecords.accept(consumer.poll(duration));
    }

    consumer.close(Duration.ofSeconds(1));
  }

  public static <K, V> boolean topicContainsMessageThat(Consumer<K, V> consumer, Duration duration,
      Predicate<ConsumerRecord<K, V>> messageSatisfies) {

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    try (consumer) {
      while (stopWatch.getTime() < duration.toMillis()) {
        ConsumerRecords<K, V> consumerRecords = consumer.poll(duration);
        for (ConsumerRecord<K, V> record : consumerRecords) {
          if (messageSatisfies.test(record)) {
            return true;
          }
        }
      }
      return false;
    }
  }

  public static <K, V> boolean topicContainsMessageThat(Duration duration,
      Predicate<ConsumerRecord<K, V>> messageSatisfies) {

    Consumer<K, V> consumer = (Consumer<K, V>) createConsumer();
    return topicContainsMessageThat(consumer, duration, messageSatisfies);
  }

  public static <K, V> boolean topicDoesNotContainMessageThat(Duration duration,
      Predicate<ConsumerRecord<K, V>> messageSatisfies) {

    return !topicContainsMessageThat(duration, messageSatisfies);
  }
}
