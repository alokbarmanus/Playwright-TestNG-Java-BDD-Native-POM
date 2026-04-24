@regression @login_page
Feature: Login Functionality
  As a registered OrangeHRM user
  I want to log in to the application
  So that I can access the dashboard

  Background:
    Given the user is on the login page

  @smoke @login_valid
  Scenario: LoginPage 01: Successful login with valid credentials
    When the user enters username "Admin" and password "admin123"
    Then the user should be redirected to the dashboard
    And the dashboard header should display "Dashboard"

  @negative @login_invalid
  Scenario: LoginPage 02: Login with invalid credentials
    When the user enters username "invalid_user" and password "invalid_pass"
    Then an error message "Invalid credentials" should be displayed

  @negative @login_outline
  Scenario Outline: LoginPage 03 Login attempts with multiple invalid credential sets
    When the user enters username "<username>" and password "<password>"
    Then an error message "<error_message>" should be displayed

    Examples:
      | username     | password  | error_message       |
      | invalid_user | admin123  | Invalid credentials |
      | Admin        | wrongpass | Invalid credentials |
      |              |           | Required            |

  @smoke @login_json_data
  @dataFile:env/${env}/data/loginData.json
  Scenario: LoginPage 04: Successful login with plain JSON data
    When the user enters username "${username}" and password "${password}"
    Then the user should be redirected to the dashboard
    And the dashboard header should display "Dashboard"

  @smoke @json_map_data
  @dataFile:env/${env}/data/registrationDataMap.json
  Scenario: LoginPage 05: Successful login with JSON data as Map
    When the user enters username and password from "${loginData}" to login application
    And the user enters address information from "${addressData}" in the form
    
  @smoke @single_registration_data
  @dataFile:env/${env}/data/registrationDataSingle.json
  Scenario: LoginPage 05: Successful login with JSON data from nested JSON file
    When the user enters username "${username}" and password "${password}"
    And the user enters address information from "${addressData}" in the form 