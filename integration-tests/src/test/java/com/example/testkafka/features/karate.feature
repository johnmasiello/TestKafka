Feature: Play with Karate

  Background:
    * def computerGuessNumber =
      """
      function() {
        var Util = Java.type('com.example.testkafka.util.Util');
        return Util.magicNumber();
      }
      """

  Scenario: Tautology - guess my number succeeds
    Given def myNumber = 3
    When def guessNumber = 3
    Then match guessNumber == myNumber
    And print 'my number is ' + myNumber

  Scenario: Tautology - guess my number fails
    Given def myNumber = 3
    When def guessNumber = 5
    Then match guessNumber != myNumber
    And print 'my number is ' + myNumber
    And print 'the guess is ' + guessNumber

  Scenario: Tautology - guess my number cheating succeeds
    Given def myNumber = 3
    When def guessNumber = myNumber
    Then match guessNumber == myNumber
    And print 'my number is ' + myNumber
    And print 'you already have my number'

  Scenario: Tautology - guess my number using a computer program succeeds
    Given def myNumber = 3
    When def guessNumber = call computerGuessNumber
    Then match guessNumber == myNumber
    And print 'my number is ' + myNumber
    And print 'you already have my number'