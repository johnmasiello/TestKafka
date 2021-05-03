package com.example.testKafka.embedded;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import com.example.testKafka.util.ContainerUtil;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class StepDefsEmbeddedKafka extends EmbeddedKafkaTest {

  @When("^a producer pushes a message onto topic$")
  public void producer_pushes_message_onto_topic() throws InterruptedException {
    Duration waitDuration = Duration.ofSeconds(5);
    producer.send(topic, "Sending with own simple KafkaProducer");
    Thread.sleep(waitDuration.toMillis());
    ContainerUtil.addConsumerToContainer(testConsumer, consumerFactory, topic, groupId);
    testConsumer.getLatch().await(waitDuration.getSeconds(), TimeUnit.SECONDS);
  }

  @Then("^kafka consumer consumes the message$")
  public void consumer_consumes_message_from_topic() {
    assertThat(testConsumer.getLatch().getCount(), equalTo(1L));
    assertThat(testConsumer.getPayload(), nullValue());
  }
}
