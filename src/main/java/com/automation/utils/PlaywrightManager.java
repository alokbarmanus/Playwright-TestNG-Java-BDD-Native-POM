package com.automation.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Thread-safe manager for Playwright resources.
 * Uses ThreadLocal storage so each test thread owns its own
 * Playwright / Browser / BrowserContext / Page instance.
 *
 * <p>Supports both local browser launch and remote Playwright server
 * connection, controlled by {@code remote.execution} in
 * {@code application.properties}.
 */
public class PlaywrightManager {

    private static final ThreadLocal<Playwright>      playwrightTL      = new ThreadLocal<>();
    private static final ThreadLocal<Browser>         browserTL         = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext>  browserContext1TL = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext>  browserContext2TL = new ThreadLocal<>();
    private static final ThreadLocal<Page>            page1TL           = new ThreadLocal<>();
    private static final ThreadLocal<Page>            page2TL           = new ThreadLocal<>();

    private PlaywrightManager() {
        // utility class — no instantiation
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Initialises Playwright, launches (or connects to) the configured browser
     * and creates a new BrowserContext and Page. Call once per scenario.
     */
    public static void initPlaywright() {
        Playwright playwright = Playwright.create();
        playwrightTL.set(playwright);

        Browser browser;

        if (ConfigReader.isRemoteExecution()) {
            // ── Remote: connect to a running Playwright browser server ────────
            String wsUrl = ConfigReader.getRemoteServerUrl();
            System.out.println("[PlaywrightManager] Connecting to remote server: " + wsUrl);
            BrowserType browserType = resolveBrowserType(playwright);
            browser = browserType.connect(wsUrl);
        } else {
            // ── Local: launch a new browser process ───────────────────────────
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(ConfigReader.isHeadless());
            browser = resolveBrowserType(playwright).launch(launchOptions);
        }

        browserTL.set(browser);

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1280, 720)
                .setIgnoreHTTPSErrors(true);

        // Create two contexts and pages
        BrowserContext context1 = browser.newContext(contextOptions);
        context1.setDefaultTimeout(ConfigReader.getDefaultTimeout());
        browserContext1TL.set(context1);
        Page page1 = context1.newPage();
        page1TL.set(page1);

        BrowserContext context2 = browser.newContext(contextOptions);
        context2.setDefaultTimeout(ConfigReader.getDefaultTimeout());
        browserContext2TL.set(context2);
        Page page2 = context2.newPage();
        page2TL.set(page2);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------


    /** Returns the first {@link Page} bound to the current thread. */
    public static Page getPage() {
        return page1TL.get();
    }

    /** Returns the second {@link Page} bound to the current thread. */
    public static Page getPage2() {
        return page2TL.get();
    }

    /** Returns the first {@link BrowserContext} bound to the current thread. */
    public static BrowserContext getBrowserContext1() {
        return browserContext1TL.get();
    }

    /** Returns the second {@link BrowserContext} bound to the current thread. */
    public static BrowserContext getBrowserContext2() {
        return browserContext2TL.get();
    }

    /** Returns the {@link Browser} bound to the current thread. */
    public static Browser getBrowser() {
        return browserTL.get();
    }

    // -------------------------------------------------------------------------
    // Teardown
    // -------------------------------------------------------------------------

    /**
     * Closes Page, BrowserContext, Browser and Playwright in order and removes
     * all ThreadLocal references to prevent memory leaks.
     */
    public static void closePlaywright() {
        closeSafely(page1TL);
        closeSafely(page2TL);
        closeSafely(browserContext1TL);
        closeSafely(browserContext2TL);
        closeSafely(browserTL);
        closeSafely(playwrightTL);
    }

    //@SuppressWarnings("unchecked")
    private static <T extends AutoCloseable> void closeSafely(ThreadLocal<T> threadLocal) {
        T resource = threadLocal.get();
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Warning: error while closing resource — " + e.getMessage());
            } finally {
                threadLocal.remove();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Resolves the {@link BrowserType} from the {@code browser.name} config key.
     * Defaults to Chromium when the value is unrecognised.
     */
    private static BrowserType resolveBrowserType(Playwright playwright) {
        return switch (ConfigReader.getBrowser().toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit"  -> playwright.webkit();
            default        -> playwright.chromium();
        };
    }
}

