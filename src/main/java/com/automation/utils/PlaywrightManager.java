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
    private static final ThreadLocal<BrowserContext>  browserContextTL  = new ThreadLocal<>();
    private static final ThreadLocal<Page>            pageTL            = new ThreadLocal<>();

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

        BrowserContext context = browser.newContext(contextOptions);
        context.setDefaultTimeout(ConfigReader.getDefaultTimeout());
        browserContextTL.set(context);

        Page page = context.newPage();
        pageTL.set(page);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns the {@link Page} bound to the current thread. */
    public static Page getPage() {
        return pageTL.get();
    }

    /** Returns the {@link BrowserContext} bound to the current thread. */
    public static BrowserContext getBrowserContext() {
        return browserContextTL.get();
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
        closeSafely(pageTL);
        closeSafely(browserContextTL);
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

