Feature: Topics

  Background:
    * callonce read('callOnce.feature')
    * def KafkaUtil = Java.type('com.example.testkafka.util.topics.KafkaUtil')
    * def Conditions = Java.type('com.example.testkafka.util.topics.TopicConditions')
    * def waitOnConsumer = function(){ Util.waitFor(LONG_DELAY_MS, 'Waiting on Consumer Topic') }
    # Each scenario will effectively reset the state of the topic by auto-committing to the
    # highest value of offset for the (partition of the) consumer group
    * KafkaUtil.purgeAllMessagesInTopic(MEDIUM_DELAY_MS)

  @dev
  @stage
  Scenario: Consumer consumes message with Id 123
    Given def payload = topicPayloads.TopicMessage
    When KafkaUtil.publishPayload(payload)
    And call waitOnConsumer
    Then def result = Conditions.topicNoLongerContainsMessageWithID123(MEDIUM_DELAY_MS)
    And match result == true

  @dev
  @stage
  Scenario: Consumer does not consume message with Id 123
    Given def payload = topicPayloads.TopicMessage
    When KafkaUtil.publishPayload(payload)
    And call waitOnConsumer
    Then def result = Conditions.topicStillContainsMessageWithID123(MEDIUM_DELAY_MS)
    And match result == true