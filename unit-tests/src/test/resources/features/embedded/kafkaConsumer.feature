Feature: Kafka processor handles events
  Scenario: Kafka consumer consumes a particular message
    When a producer pushes a message onto topic
    Then kafka consumer consumes the message