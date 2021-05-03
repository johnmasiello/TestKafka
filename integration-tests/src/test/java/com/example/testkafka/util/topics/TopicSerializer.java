package com.example.testkafka.util.topics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.example.testkafka.models.TopicMessage;
import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

public class TopicSerializer implements Serializer<Object>, Deserializer<Object> {
  private ObjectReader objectReader = objectReader();

  private ObjectWriter objectWriter = objectWriter();

  public static ObjectMapper objectMapper() {
    return new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  public static ObjectReader objectReader() {
    return objectMapper().readerFor(TopicMessage.class);
  }

  public static ObjectWriter objectWriter() {
    return objectMapper().writerFor(Map.class);
  }

  @Override
  public Object deserialize(String s, byte[] bytes) {
    try {
      return objectReader.readValue(bytes);

    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public Object deserialize(String topic, Headers headers, byte[] data) {
    try {
      return objectReader.readValue(data);

    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {

  }

  @Override
  public byte[] serialize(String s, Object o) {
    try {
      return objectWriter.writeValueAsBytes(o);

    } catch (JsonProcessingException e) {
      return new byte[0];
    }
  }

  @Override
  public byte[] serialize(String topic, Headers headers, Object data) {
    try {
      return objectWriter.writeValueAsBytes(data);

    } catch (JsonProcessingException e) {
      return new byte[0];
    }
  }

  @Override
  public void close() {

  }
}
