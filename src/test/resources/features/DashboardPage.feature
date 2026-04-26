@regression @dashboard
Feature: Dashboard Page Functionality
  As a logged-in OrangeHRM user
  I want to view the Dashboard page
  So that I can access key information and quick navigation links

  Background:
    Given the user is logged into OrangeHRM as "Admin" with password "admin123"
    And the user is on the dashboard page

  # ---------------------------------------------------------------------------
  # Page header
  # ---------------------------------------------------------------------------

  @smoke @dashboard_header
  Scenario: Dashboard page 01: Dashboard page heading is displayed
    Then the dashboard page heading should be visible
    And the dashboard page heading should display "Dashboard"

  # ---------------------------------------------------------------------------
  # Top navigation
  # ---------------------------------------------------------------------------

  @smoke @dashboard_nav
  Scenario: Dashboard page 02: Top navigation bar is visible on the dashboard
    Then the top navigation bar should be visible

#   @smoke @dashboard_nav
#   Scenario: Logged-in user name is displayed in the top navigation
#     Then the logged-in user name "Admin" should be visible in the top navigation

  @smoke @dashboard_nav
  Scenario Outline: Dashboard page 03: Top navigation menu items are visible
    Then the top navigation should contain the menu item "<menuItem>"

    Examples:
      | menuItem  |
      | Dashboard |
      | Admin     |
      | Leave     |
      | Time      |
      | PIM       |

  # ---------------------------------------------------------------------------
  # Quick Launch widget
  # ---------------------------------------------------------------------------

  @smoke @dashboard_quick_launch
  Scenario: Dashboard page 04: Quick Launch widget is displayed
    Then the Quick Launch widget should be visible

  @smoke @dashboard_quick_launch
  Scenario Outline: Dashboard page 05: All Quick Launch shortcuts are visible
    Then the Quick Launch shortcut "<shortcut>" should be visible

    Examples:
      | shortcut     |
      | Assign Leave |
      | Leave List   |
      | Timesheets   |
      | Apply Leave  |
      | My Leave     |
      | My Timesheet |

  # ---------------------------------------------------------------------------
  # Dashboard widgets
  # ---------------------------------------------------------------------------

  # @smoke @dashboard_widgets
  # Scenario Outline: Dashboard page 06: Key dashboard widgets are visible
  #   Then the dashboard widget "<widgetName>" should be visible

  #   Examples:
  #     | widgetName              |
  #     | Time at Work            |
  #     | My Actions              |
  #     | Today's Leave           |
  #     | Employees on Leave Today|

  # ---------------------------------------------------------------------------
  # Navigation from Quick Launch
  # ---------------------------------------------------------------------------

  # @dashboard_quick_launch @navigation
  # Scenario: Dashboard page 07: Clicking Assign Leave shortcut navigates to the Assign Leave page
  #   When the user clicks the "Assign Leave" Quick Launch shortcut
  #   Then the page URL should contain "viewLeaveList"

  @dashboard_quick_launch @navigation @dashboardpage08
  Scenario: Dashboard page 08: Clicking Timesheets shortcut navigates to the Timesheets page
    When the user clicks the "Timesheets" Quick Launch shortcut
    Then the page URL should contain "viewMyTimesheetList"
