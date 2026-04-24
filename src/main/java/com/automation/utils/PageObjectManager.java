package com.automation.utils;

import com.automation.pages.DashboardPageObjects;
import com.automation.pages.LoginPageObjects;

/**
 * Centralised factory for all Page Object instances.
 *
 * <p>Cucumber PicoContainer creates <strong>one {@code PageObjectManager} per
 * scenario</strong> and injects the same instance into every step-definition
 * class that declares it as a constructor parameter. This means all step
 * classes share the same page objects without any {@code new} keyword in step
 * methods.
 *
 * <p>Page objects are created <em>lazily</em> on first access. This is
 * important because {@link PlaywrightManager} — and therefore the Playwright
 * {@link com.microsoft.playwright.Page} — is initialised in the Cucumber
 * {@code @Before} hook, which runs <em>before</em> any step but
 * <em>after</em> PicoContainer constructs this class.
 */
public class PageObjectManager {

    private LoginPageObjects     loginPage;
    private DashboardPageObjects dashboardPage;

    // -------------------------------------------------------------------------
    // Page object accessors (lazy initialisation)
    // -------------------------------------------------------------------------

    /**
     * Returns the {@link LoginPageObjects} instance for the current scenario,
     * creating it on the first call.
     */
    public LoginPageObjects getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPageObjects();
        }
        return loginPage;
    }

    /**
     * Returns the {@link DashboardPageObjects} instance for the current scenario,
     * creating it on the first call.
     */
    public DashboardPageObjects getDashboardPage() {
        if (dashboardPage == null) {
            dashboardPage = new DashboardPageObjects();
        }
        return dashboardPage;
    }
}
