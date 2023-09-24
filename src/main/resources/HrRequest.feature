@smoke
Feature: Hr Request
  Scenario Outline: user can request an Hr request and Hr approve it

    Given user can request hr <ll>,<ln>,<sal>
    Then hr approve <app>

   Examples:
      |ll|ln|sal|app|
     |hy|English|true|approve|
