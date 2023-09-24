@smoke
Feature: Training Request
  Scenario Outline: employee request training
    When user request training<Name>,<Impact>
    And manager approve or reject
    And director approve or reject
    Then l&d approve or reject
    Examples:
    |Name|Impact|
    |testing|good|
