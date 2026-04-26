package com.automation.stepdefinitions;

import com.automation.utils.PageObjectManager;
import com.automation.utils.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.automation.utils.ExtentManager;
import org.testng.Reporter;

import java.util.Map;

/**
 * Step definitions for the Login feature.
 *
 * Both {@link PageObjectManager} and {@link ScenarioContext} are injected by
 * Cucumber PicoContainer. {@link ScenarioContext#resolve(String)} transparently
 * substitutes {@code ${key}} placeholders with values loaded from the JSON
 * test-data file declared via the {@code @dataFile:} scenario tag.
 */
public class LoginPageStepDefinitions {

    private final PageObjectManager pom;
    private final ScenarioContext context;

    public LoginPageStepDefinitions(PageObjectManager pom, ScenarioContext context) {
        this.pom = pom;
        this.context = context;
    }

    // -------------------------------------------------------------------------
    // Given
    // -------------------------------------------------------------------------

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        ExtentManager.getTest().info("Navigating to login page");
        Reporter.log("[Step] Navigating to login page", true);
        pom.getLoginPage().navigateToLoginPage();
    }

    // -------------------------------------------------------------------------
    // When
    // -------------------------------------------------------------------------

    @When("the user enters username {string} and password {string}")
    public void theUserEntersUsernameAndPassword(String username, String password) {
        // Log parameters with ExtentReports
        ExtentManager.getTest().info("Step: Enter credentials");
        ExtentManager.getTest().info("Username: <b>" + username + "</b>");
        ExtentManager.getTest().info("Password: <b>" + password + "</b>");
        Reporter.log(String.format("[Step] Entering username: %s and password: %s", username, password), true);
        pom.getLoginPage().login(context.resolve(username), context.resolve(password));

        // Example: Attach a screenshot after login (for demonstration)
        /*try {
            com.microsoft.playwright.Page page = pom.getLoginPage().getPage();
            if (page != null) {
                String screenshotPath = "target/cucumber-reports/LOGIN_SCREENSHOT_" + System.currentTimeMillis() + ".png";
                page.screenshot(new com.microsoft.playwright.Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(screenshotPath)).setFullPage(true));
                ExtentManager.getTest().info("Login page screenshot:").addScreenCaptureFromPath("../cucumber-reports/" + new java.io.File(screenshotPath).getName());
            }
        } catch (Exception ex) {
            ExtentManager.getTest().warning("Could not capture login screenshot: " + ex.getMessage());
        }*/
        ExtentManager.logInfo("the user enters username \"username\" and password \"password\"");
        // Example: Log a pass message for successful login step
        ExtentManager.getTest().pass("Login step executed successfully.");
    }

    @When("the user enters username and password from {string} to login application")
    public void the_user_enters_username_and_password_from_to_login_application(String dataRef) {
        Map<String, String> loginData = context.resolveMap(dataRef);
        ExtentManager.getTest().info("Login using dataRef: " + dataRef);
        Reporter.log("[Step] Login using dataRef: " + dataRef, true);
        pom.getLoginPage().login(loginData.get("username"), loginData.get("password"));
        ExtentManager.logInfo("the user enters username and password from {loginData} to login application");
        ExtentManager.getTest().pass("Login step executed successfully with logindata: " + dataRef);
    }

    // @When("the user enters address information from {string} in the form")
    // public void the_user_enters_address_information_from_in_the_form(String
    // dataRef) {
    // Map<String, String> addressData = context.resolveMap(dataRef);
    // System.out.println("[Step] Address information:");
    // addressData.forEach((key, value) -> System.out.println(" " + key + ": " +
    // value));
    // }

    @When("the user enters address information from {string} in the form")
    public void the_user_enters_address_information_from_in_the_form(String dataRef) {
        Map<String, String> addressData = context.resolveMap(dataRef);

        String street = addressData.get("street");
        String city = addressData.get("city");
        String state = addressData.get("state");
        String zip = addressData.get("zip");

        String msg = String.format("Address info: street=%s, city=%s, state=%s, zip=%s", street, city, state, zip);
        ExtentManager.getTest().info(msg);
        Reporter.log("[Step] " + msg, true);

        // Use with a page object, e.g.:
        // pom.getRegistrationPage().fillAddressForm(street, city, state, zip);
    }
    // -------------------------------------------------------------------------
    // Then
    // -------------------------------------------------------------------------

    @Then("the user should be redirected to the dashboard")
    public void theUserShouldBeRedirectedToTheDashboard() {
        try {
            assertThat(pom.getDashboardPage().getDashboardHeaderLocator()).isVisible();
            Reporter.log("[Step] User redirected to dashboard", true);
            ExtentManager.logInfo("the user should be redirected to the dashboard");
            ExtentManager.getTest().pass("User redirected to dashboard");
        } catch (AssertionError e) {
            ExtentManager.getTest().fail("User not redirected to dashboard: " + e.getMessage());
            Reporter.log("[FAIL] User not redirected to dashboard: " + e.getMessage(), true);
            throw e;
        }
    }

    @And("the dashboard header should display {string}")
    public void theDashboardHeaderShouldDisplay(String expectedHeader) {
        try {
            assertThat(pom.getDashboardPage().getDashboardHeaderLocator())
                    .hasText(context.resolve(expectedHeader));
            Reporter.log("[Step] Dashboard header displayed: " + expectedHeader, true);
            ExtentManager.logInfo("the dashboard header should display: "+expectedHeader);
            ExtentManager.getTest().pass("Dashboard header displayed: " + expectedHeader);
            
        } catch (AssertionError e) {
            ExtentManager.getTest().fail("Dashboard header not displayed: " + e.getMessage());
            Reporter.log("[FAIL] Dashboard header not displayed: " + e.getMessage(), true);
            throw e;
        }
    }

    @Then("an error message {string} should be displayed")
    public void anErrorMessageShouldBeDisplayed(String expectedError) {
        try {
            assertThat(pom.getLoginPage().getErrorLocator())
                    .containsText(context.resolve(expectedError));
            ExtentManager.logInfo("an error message should be displayed: "+expectedError);
            ExtentManager.getTest().pass("Error message displayed: " + expectedError);
            Reporter.log("[Step] Error message displayed: " + expectedError, true);
        } catch (AssertionError e) {
            ExtentManager.getTest().fail("Error message not displayed: " + e.getMessage());
            Reporter.log("[FAIL] Error message not displayed: " + e.getMessage(), true);
            throw e;
        }
    }

    // private void attachScreenshotOnFailure() {
    //     try {
    //         Page page = pom.getLoginPage().getPage();
    //         String screenshotDir = "target/cucumber-reports/img";
    //         java.nio.file.Files.createDirectories(java.nio.file.Paths.get(screenshotDir)); // Ensure directory exists
    //         if (page != null) {
    //             String screenshotPath = screenshotDir + "/FAILURE_SCREENSHOT_" + System.currentTimeMillis() + ".png";
    //             page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(screenshotPath)).setFullPage(true));
    //             ExtentManager.getTest().addScreenCaptureFromPath("../cucumber-reports/img/" + new java.io.File(screenshotPath).getName());
    //             Reporter.log("<a href='../cucumber-reports/img/" + new java.io.File(screenshotPath).getName() + "'>Screenshot</a>", true);
    //         }
    //     } catch (Exception ex) {
    //         ExtentManager.getTest().warning("Failed to capture screenshot: " + ex.getMessage());
    //         Reporter.log("[WARN] Failed to capture screenshot: " + ex.getMessage(), true);
    //     }
    // }
}
