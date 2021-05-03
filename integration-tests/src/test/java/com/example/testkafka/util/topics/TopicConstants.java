package com.example.testkafka.util.topics;

public class TopicConstants {

  public static final String KAFKA_BROKERS = "localhost:9092";

  public static final String CLIENT_ID = "integration-client";

  public static final String TOPIC_NAME = "testKafka";

  public static final String GROUP_ID_CONFIG = "c1";

  public static final String OFFSET_RESET_LATEST = "latest";

  public static final String OFFSET_RESET_EARLIER = "earliest";

  public static final Integer MAX_POLL_RECORDS = 1;

  public static final Integer MAX_POLL_RECORDS_ON_PURGE = 100;

  public static final Boolean ENABLE_AUTO_COMMIT = true;

  public static final Integer MAX_BLOCK_MS_ON_SEND = 5000;

  public static final Integer RETRIES_ON_SEND = 1;
}
