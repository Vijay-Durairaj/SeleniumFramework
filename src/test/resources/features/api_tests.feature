@api
Feature: API Tests for E-Commerce Application

  Background:
    Given I have the API base URL configured

  @api_login
  Scenario: Successful login returns a valid token
    When I send a POST request to login with valid credentials
    Then the response status code should be 200
    And the response body should contain a token

  @api_login
  Scenario: Login with invalid credentials returns 401
    When I send a POST request to login with invalid credentials
    Then the response status code should be 401

  @api_products
  Scenario: Authenticated user can fetch all products
    Given I am logged in via the API
    When I send a GET request to fetch all products
    Then the response status code should be 200
    And the product list should not be empty
