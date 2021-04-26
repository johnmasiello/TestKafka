Feature: Kafka processor handles events (test container)
  Scenario: Kafka consumer consumes a particular message (test container)
    When a producer pushes a message onto topic (test container)
    Then kafka consumer consumes the message (test container)