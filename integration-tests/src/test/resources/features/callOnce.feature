@ignore
Feature: Initialization

  Scenario:
    * def topicPayloads = read('classpath:payloads/payloads.json')
    * def Util = Java.type('com.example.testkafka.util.Util')
    * def MEDIUM_DELAY_MS = 5000
    * def LONG_DELAY_MS = 10000