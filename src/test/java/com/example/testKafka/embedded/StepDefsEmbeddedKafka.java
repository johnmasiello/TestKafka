package com.example.testKafka.embedded;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.concurrent.TimeUnit;

public class StepDefsEmbeddedKafka extends EmbeddedKafkaIntegrationTest {

  @When("^a producer pushes a message onto topic$")
  public void producer_pushes_message_onto_topic() throws InterruptedException {
    producer.send(topic, "Sending with own simple KafkaProducer");
    consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
  }

  @Then("^kafka consumer consumes the message$")
  public void consumer_consumes_message_from_topic() {
    assertThat(consumer.getLatch().getCount(), equalTo(0L));
    assertThat(consumer.getPayload(), containsString("embedded-test-topic"));
  }
}
