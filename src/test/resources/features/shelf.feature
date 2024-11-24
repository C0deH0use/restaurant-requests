# Created by marek.macbook at 27/09/2024
Feature: Shelf - Collecting menu items for a given Request
  After request is created, or after kitchen events request menu items packing will occur.

  Scenario Outline: The request should collect as many menu items as possible that are available on the shelf at the moment when the restaurant worker is collecting the order.
    Depending on the collection status, the request status can set to a number of status: READY_TO_COLLECT, REQUESTED_ITEMS
    In case of items needed to be prepared by kitchen workers, a new request is send for a given menu item and the amount of items to be created.

    Given the shelf contains <shelf items> menu items from request
    When handling requested <request cnt> Menu Items
    Then request should be updated with <collected items> prepared menu items from the shelf
    And shelf should be updated with <shelf remaining items> menu items taken for request
    And <kitchen requested items> menu items should be requested by the restaurant worker
    And request Status should be set to <expected request status>
    Examples:
      | shelf items | request cnt | shelf remaining items | collected items | kitchen requested items | expected request status |
      | 10          | 4           | 6                     | 4               | 0                       | READY_TO_COLLECT        |
      | 4           | 5           | 0                     | 4               | 1                       | REQUESTED_ITEMS         |
      | 0           | 5           | 0                     | 0               | 5                       | REQUESTED_ITEMS         |


  Scenario: When requested new menu items that is not known on the shelf yet.
    Restaurant workers will create new Shelf item and request from kitchen the required menu items
    the status should be: REQUESTED_ITEMS

    Given shelf not containing any items
    When request a new Menu Item
    Then request Status should be set to REQUESTED_ITEMS
    And 1 menu items should be requested by the restaurant worker
    And new shelf item should be created with 0 menu items
