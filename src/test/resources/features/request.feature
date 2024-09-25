# Created by marek.macbook at 22/09/2024
Feature: Request
  As a user I want to request a new restaurant order

  Scenario: Create new request
    Given customer requests known menu items
    When creating new request
    Then new request is created

  Scenario: Fail to create new request
    Given customer requests any of the menu items not being known to the restaurant
    When creating new request
    Then no request is created
