Feature: As a customer, I wish to view the contents of my bag prior to checkout

  Scenario: Add shirt to bag and view bag
    Given I have added a shirt to my bag
    When I view the contents of my bag
    Then I can see the contents of the bag include a shirt