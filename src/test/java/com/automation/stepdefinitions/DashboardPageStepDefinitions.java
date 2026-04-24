package com.automation.stepdefinitions;

import com.automation.utils.PageObjectManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;
import com.automation.utils.ExtentManager;
import org.testng.Reporter;
import com.microsoft.playwright.Page;

/**
 * Step definitions for the Dashboard feature.
 *
 * {@link PageObjectManager} is injected by Cucumber PicoContainer — no
 * {@code new} keyword needed in step methods.
 */
public class DashboardPageStepDefinitions {

    private final PageObjectManager pom;

    public DashboardPageStepDefinitions(PageObjectManager pom) {
        this.pom = pom;
    }

    // -------------------------------------------------------------------------
    // Background steps
    // -------------------------------------------------------------------------

    @Given("the user is logged into OrangeHRM as {string} with password {string}")
    public void theUserIsLoggedIntoOrangeHRMAs(String username, String password) {
        ExtentManager.getTest().info(String.format("Login as %s / %s", username, password));
        Reporter.log(String.format("[Step] Login as %s / %s", username, password), true);
        pom.getLoginPage().navigateToLoginPage();
        pom.getLoginPage().login(username, password);
    }

    @And("the user is on the dashboard page")
    public void theUserIsOnTheDashboardPage() {
        ExtentManager.getTest().info("Waiting for dashboard page");
        Reporter.log("[Step] Waiting for dashboard page", true);
        pom.getDashboardPage().waitForDashboard();
    }

    // -------------------------------------------------------------------------
    // Page header
    // -------------------------------------------------------------------------

    @Then("the dashboard page heading should be visible")
    public void theDashboardPageHeadingShouldBeVisible() {
        try {
            assertThat(pom.getDashboardPage().getDashboardHeaderLocator()).isVisible();
            ExtentManager.getTest().pass("Dashboard heading is visible");
            Reporter.log("[Step] Dashboard heading is visible", true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("Dashboard heading not visible: " + e.getMessage());
            Reporter.log("[FAIL] Dashboard heading not visible: " + e.getMessage(), true);
            throw e;
        }
    }

    @And("the dashboard page heading should display {string}")
    public void theDashboardPageHeadingShouldDisplay(String expectedHeading) {
        try {
            assertThat(pom.getDashboardPage().getDashboardHeaderLocator()).hasText(expectedHeading);
            ExtentManager.getTest().pass("Dashboard heading displayed: " + expectedHeading);
            Reporter.log("[Step] Dashboard heading displayed: " + expectedHeading, true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("Dashboard heading not displayed: " + e.getMessage());
            Reporter.log("[FAIL] Dashboard heading not displayed: " + e.getMessage(), true);
            throw e;
        }
    }

    // -------------------------------------------------------------------------
    // Top navigation
    // -------------------------------------------------------------------------

    @Then("the top navigation bar should be visible")
    public void theTopNavigationBarShouldBeVisible() {
        try {
            assertThat(pom.getDashboardPage().getTopNavBarLocator()).isVisible();
            ExtentManager.getTest().pass("Top navigation bar is visible");
            Reporter.log("[Step] Top navigation bar is visible", true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("Top navigation bar not visible: " + e.getMessage());
            Reporter.log("[FAIL] Top navigation bar not visible: " + e.getMessage(), true);
            throw e;
        }
    }

    @Then("the logged-in user name {string} should be visible in the top navigation")
    public void theLoggedInUserNameShouldBeVisibleInTheTopNavigation(String expectedName) {
        try {
            assertThat(pom.getDashboardPage().getUserDropdownNameLocator()).containsText(expectedName);
            ExtentManager.getTest().pass("User name visible in top navigation: " + expectedName);
            Reporter.log("[Step] User name visible in top navigation: " + expectedName, true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("User name not visible in top navigation: " + e.getMessage());
            Reporter.log("[FAIL] User name not visible in top navigation: " + e.getMessage(), true);
            throw e;
        }
    }

    @Then("the top navigation should contain the menu item {string}")
    public void theTopNavigationShouldContainTheMenuItem(String menuItem) {
        try {
            assertThat(pom.getDashboardPage().getMainMenuItemLocator(menuItem)).isVisible();
            ExtentManager.getTest().pass("Menu item visible: " + menuItem);
            Reporter.log("[Step] Menu item visible: " + menuItem, true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("Menu item not visible: " + e.getMessage());
            Reporter.log("[FAIL] Menu item not visible: " + e.getMessage(), true);
            throw e;
        }
    }

    // -------------------------------------------------------------------------
    // Quick Launch widget
    // -------------------------------------------------------------------------

    @Then("the Quick Launch widget should be visible")
    public void theQuickLaunchWidgetShouldBeVisible() {
        try {
            assertThat(pom.getDashboardPage().getQuickLaunchCardLocator()).isVisible();
            ExtentManager.getTest().pass("Quick Launch widget is visible");
            Reporter.log("[Step] Quick Launch widget is visible", true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("Quick Launch widget not visible: " + e.getMessage());
            Reporter.log("[FAIL] Quick Launch widget not visible: " + e.getMessage(), true);
            throw e;
        }
    }

    @Then("the Quick Launch shortcut {string} should be visible")
    public void theQuickLaunchShortcutShouldBeVisible(String shortcutName) {
        try {
            assertThat(pom.getDashboardPage().getQuickLaunchShortcutLocator(shortcutName)).isVisible();
            ExtentManager.getTest().pass("Quick Launch shortcut visible: " + shortcutName);
            Reporter.log("[Step] Quick Launch shortcut visible: " + shortcutName, true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("Quick Launch shortcut not visible: " + e.getMessage());
            Reporter.log("[FAIL] Quick Launch shortcut not visible: " + e.getMessage(), true);
            throw e;
        }
    }

    // -------------------------------------------------------------------------
    // Dashboard widgets
    // -------------------------------------------------------------------------

    @Then("the dashboard widget {string} should be visible")
    public void theDashboardWidgetShouldBeVisible(String widgetName) {
        try {
            switch (widgetName) {
                case "Time at Work"             -> assertThat(pom.getDashboardPage().getTimeAtWorkCardLocator()).isVisible();
                case "My Actions"               -> assertThat(pom.getDashboardPage().getMyActionsCardLocator()).isVisible();
                case "Today's Leave"            -> assertThat(pom.getDashboardPage().getTodaysLeaveCardLocator()).isVisible();
                case "Employees on Leave Today" -> assertThat(pom.getDashboardPage().getEmployeesOnLeaveCardLocator()).isVisible();
                default -> throw new IllegalArgumentException("Unknown widget: " + widgetName);
            }
            ExtentManager.getTest().pass("Dashboard widget visible: " + widgetName);
            Reporter.log("[Step] Dashboard widget visible: " + widgetName, true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("Dashboard widget not visible: " + e.getMessage());
            Reporter.log("[FAIL] Dashboard widget not visible: " + e.getMessage(), true);
            throw e;
        }
    }

    // -------------------------------------------------------------------------
    // Quick Launch navigation
    // -------------------------------------------------------------------------

    @When("the user clicks the {string} Quick Launch shortcut")
    public void theUserClicksTheQuickLaunchShortcut(String shortcutName) {
        ExtentManager.getTest().info("Clicking Quick Launch shortcut: " + shortcutName);
        Reporter.log("[Step] Clicking Quick Launch shortcut: " + shortcutName, true);
        switch (shortcutName) {
            case "Assign Leave" -> pom.getDashboardPage().clickAssignLeaveShortcut();
            case "Leave List"   -> pom.getDashboardPage().clickLeaveListShortcut();
            case "Timesheets"   -> pom.getDashboardPage().clickTimesheetsShortcut();
            default -> throw new IllegalArgumentException(
                    "No click action mapped for shortcut: " + shortcutName);
        }
        pom.getDashboardPage().waitForNetworkIdle();
    }

    @Then("the page URL should contain {string}")
    public void thePageUrlShouldContain(String urlFragment) {
        try {
            assertTrue(
                    pom.getDashboardPage().getCurrentUrl().contains(urlFragment),
                    "Expected URL to contain '" + urlFragment + "' but was: "
                            + pom.getDashboardPage().getCurrentUrl());
            ExtentManager.getTest().pass("Page URL contains: " + urlFragment);
            Reporter.log("[Step] Page URL contains: " + urlFragment, true);
        } catch (AssertionError e) {
            attachScreenshotOnFailure();
            ExtentManager.getTest().fail("Page URL does not contain: " + urlFragment + ". " + e.getMessage());
            Reporter.log("[FAIL] Page URL does not contain: " + urlFragment + ". " + e.getMessage(), true);
            throw e;
        }
    }

    private void attachScreenshotOnFailure() {
        try {
            Page page = pom.getDashboardPage().getPage();
            if (page != null) {
                String screenshotPath = "target/cucumber-reports/FAILURE_SCREENSHOT_" + System.currentTimeMillis() + ".png";
                page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(screenshotPath)).setFullPage(true));
                ExtentManager.getTest().addScreenCaptureFromPath("../cucumber-reports/" + new java.io.File(screenshotPath).getName());
                Reporter.log("<a href='../cucumber-reports/" + new java.io.File(screenshotPath).getName() + "'>Screenshot</a>", true);
            }
        } catch (Exception ex) {
            ExtentManager.getTest().warning("Failed to capture screenshot: " + ex.getMessage());
            Reporter.log("[WARN] Failed to capture screenshot: " + ex.getMessage(), true);
        }
    }
}

