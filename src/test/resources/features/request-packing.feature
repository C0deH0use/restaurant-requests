# Created by marek.macbook at 22/09/2024
Feature: Packing menu items
  After request is created, the packing command should be invoked and restaurant workers should try to attempt to collect all requested menu items.

  Scenario Outline:

    Given <collected menu items> out of <total menu items> total menu items got collected from shelf
    When packing request
    Then request status should be set to <expected request status>
    And requested menu items where updated <collected menu items> times by 1
    Examples:
      | collected menu items | total menu items | expected request status |
      | 3                    | 3                | READY_TO_COLLECT        |
      | 1                    | 3                | IN_PROGRESS             |



  Scenario: When requested menu items of type 'immediate', restaurant workers will be able to prepare these on the go and add to the order.
  if the order is finished, the status should be: READY_TO_COLLECT
  else: IN_PROGRESS

    Given request containing Menu Items of type 'immediate'
    When packing request
    Then request status should be set to READY_TO_COLLECT
    And no items where picked from shelf