package com.automation.base;

import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

/**
 * Base class for all Page Objects.
 * Provides shared access to the Playwright {@link Page} and common
 * navigation / wait helpers.
 */
public class BasePage {

    protected final Page page;

    public BasePage() {
        this.page = PlaywrightManager.getPage();
    }

    /**
     * Exposes the Playwright Page instance for screenshot and advanced actions.
     */
    public Page getPage() {
        return page;
    }

    // -------------------------------------------------------------------------
    // Navigation helpers
    // -------------------------------------------------------------------------

    /** Navigates to the given URL and waits for DOMContentLoaded. */
    public void navigateTo(String url) {
        page.navigate(url);
    }

    /** Waits until the network is idle (useful after form submissions). */
    public void waitForNetworkIdle() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /** Waits for the DOM to finish loading. */
    public void waitForDomContentLoaded() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    // -------------------------------------------------------------------------
    // Page introspection helpers
    // -------------------------------------------------------------------------

    /** Returns the current page title. */
    public String getTitle() {
        return page.title();
    }

    /** Returns the current page URL. */
    public String getCurrentUrl() {
        return page.url();
    }

    // -------------------------------------------------------------------------
    // Element interaction helpers
    // -------------------------------------------------------------------------

    /** Clears the element and types the given text. */
    protected void fillField(Locator locator, String value) {
        locator.clear();
        locator.fill(value);
    }

    /** Clicks the element and waits for any resulting navigation to settle. */
    protected void clickElement(Locator locator) {
        locator.click();
    }

    /** Returns the trimmed visible text content of the element. */
    protected String getText(Locator locator) {
        return locator.textContent().trim();
    }

    /** Returns {@code true} if the element is currently visible. */
    protected boolean isVisible(Locator locator) {
        return locator.isVisible();
    }

    /**
     * Waits for the element to become visible before returning the locator.
     * Useful as a built-in guard before interactions.
     */
    protected Locator waitForVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        return locator;
    }
}
