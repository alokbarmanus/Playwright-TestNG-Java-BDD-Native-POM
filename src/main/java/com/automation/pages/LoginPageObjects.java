package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import com.microsoft.playwright.Locator;

/**
 * Page Object for the OrangeHRM Login page.
 * URL: https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
 */
public class LoginPageObjects extends BasePage {

    // -------------------------------------------------------------------------
    // Locators
    // -------------------------------------------------------------------------
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorAlert;
    private final Locator requiredErrorMessages;

    public LoginPageObjects() {
        super();
        usernameInput        = page.locator("input[name='username']");
        passwordInput        = page.locator("input[name='password']");
        loginButton          = page.locator("button[type='submit']");
        errorAlert           = page.locator(".oxd-alert-content-text");
        requiredErrorMessages = page.locator(".oxd-input-field-error-message");
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    /** Navigates to the login page using the URL from config. */
    public void navigateToLoginPage() {
        navigateTo(ConfigReader.getBaseUrl());
        waitForDomContentLoaded();
        waitForVisible(usernameInput);
    }

    /** Clears and fills the username field. */
    public void enterUsername(String username) {
        fillField(usernameInput, username);
    }

    /** Clears and fills the password field. */
    public void enterPassword(String password) {
        fillField(passwordInput, password);
    }

    /** Clicks the Login button. */
    public void clickLoginButton() {
        clickElement(loginButton);
    }

    /**
     * Convenience: fills credentials and submits the form.
     *
     * @param username login username
     * @param password login password
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        page.waitForTimeout(3000); // Small delay to mimic real user typing and allow any dynamic validation to occur
    }

    // -------------------------------------------------------------------------
    // Assertions / Getters
    // -------------------------------------------------------------------------

    /** Returns the trimmed text of the alert message shown on failed login. */
    public String getErrorAlertMessage() {
        waitForVisible(errorAlert);
        return getText(errorAlert);
    }

    /**
     * Returns the first "Required" validation error message text.
     * Visible when the form is submitted with empty fields.
     */
    public String getFirstRequiredErrorMessage() {
        requiredErrorMessages.first().waitFor();
        return getText(requiredErrorMessages.first());
    }

    /**
     * Returns the error text depending on what is visible — alert banner or
     * inline required-field error.
     */
    public String getErrorMessage() {
        if (errorAlert.isVisible()) {
            return getText(errorAlert);
        }
        requiredErrorMessages.first().waitFor();
        return getText(requiredErrorMessages.first());
    }

    /**
     * Returns a union {@link Locator} covering both the alert banner and the
     * inline required-field error, suitable for Playwright {@code assertThat}
     * assertions.
     */
    public Locator getErrorLocator() {
        return errorAlert.or(requiredErrorMessages.first());
    }

    /** Exposes the username input {@link Locator} for Playwright assertions. */
    public Locator getUsernameInputLocator() {
        return usernameInput;
    }
}
