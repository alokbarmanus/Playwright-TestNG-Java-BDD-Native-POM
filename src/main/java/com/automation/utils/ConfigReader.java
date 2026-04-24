package com.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton utility that merges two property sources for the active environment:
 *
 * <ol>
 *   <li>{@code environments/application.properties} — common, env-agnostic settings
 *       (browser, headless, timeouts, proxy, remote execution…)</li>
 *   <li>{@code environments/<env>/env.properties} — environment-specific overrides
 *       (base URL, database connection…)</li>
 * </ol>
 *
 * <p>The active environment is resolved in this priority order:
 * <ol>
 *   <li>JVM system property: {@code -Denv=sit}</li>
 *   <li>{@code default.env} key in {@code application.properties}</li>
 *   <li>Hard fallback: {@code dev}</li>
 * </ol>
 *
 * <p>Environment-specific keys always win over application-level keys.
 */
public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        // ── Step 1: load application.properties (common settings) ────────────
        Properties appProps = loadResource("environments/application.properties");

        // ── Step 2: resolve active environment name ───────────────────────────
        String env = System.getProperty("env");
        if (env == null || env.isBlank()) {
            env = appProps.getProperty("default.env", "dev");
        }
        System.out.println("[ConfigReader] Active environment: " + env);

        // ── Step 3: load <env>/env.properties (overrides) ────────────────────
        Properties envProps = loadResource("environments/" + env + "/env.properties");

        // ── Step 4: merge — application first, then env overrides on top ──────
        properties.putAll(appProps);
        properties.putAll(envProps);

        // Store resolved env name so callers can read it
        properties.setProperty("active.env", env);
    }

    private ConfigReader() {
        // utility class — no instantiation
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static Properties loadResource(String path) {
        Properties props = new Properties();
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Required config file not found on classpath: " + path);
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + path, e);
        }
        return props;
    }

    // -------------------------------------------------------------------------
    // Generic accessors
    // -------------------------------------------------------------------------

    /** Returns the value for the given key, or {@code null} if not found. */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /** Returns the value for the given key, falling back to {@code defaultValue}. */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // -------------------------------------------------------------------------
    // Convenience accessors
    // -------------------------------------------------------------------------

    /** Active environment name (e.g. {@code dev}, {@code sit}, {@code uat}). */
    public static String getActiveEnv() {
        return getProperty("active.env", "dev");
    }

    /** Application base URL from the active {@code env.properties}. */
    public static String getBaseUrl() {
        return getProperty("env.baseurl");
    }

    /** Database JDBC URL from the active {@code env.properties}. */
    public static String getDbUrl() {
        return getProperty("db.url");
    }

    /** Database username from the active {@code env.properties}. */
    public static String getDbUsername() {
        return getProperty("db.username");
    }

    /** Database password from the active {@code env.properties}. */
    public static String getDbPassword() {
        return getProperty("db.password");
    }

    /** Browser name from {@code application.properties} (default: {@code chromium}). */
    public static String getBrowser() {
        return getProperty("browser.name", "chromium");
    }

    /** Headless flag from {@code application.properties} (default: {@code false}). */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    /** Playwright wait timeout in milliseconds (default: 30000). */
    public static double getDefaultTimeout() {
        return Double.parseDouble(getProperty("playwright.wait.timeout", "30000"));
    }

    /** Playwright init retry timeout in milliseconds (default: 10000). */
    public static double getInitRetryTimeout() {
        return Double.parseDouble(getProperty("playwright.init.retry.timeout", "10000"));
    }

    /** Whether to capture screenshots on test success (1 = yes, 0 = no). */
    public static boolean isCaptureSuccessScreenshots() {
        return "1".equals(getProperty("test.success.screenshots", "0"));
    }

    /** Whether to capture screenshots on test failure (1 = yes, 0 = no). */
    public static boolean isCaptureFailureScreenshots() {
        return "1".equals(getProperty("test.failure.screenshots", "1"));
    }

    /** Scenario retry count (default: 1). */
    public static int getRetryCount() {
        return Integer.parseInt(getProperty("retry.count", "1"));
    }

    /** Whether to connect to a remote Playwright browser server. */
    public static boolean isRemoteExecution() {
        return Boolean.parseBoolean(getProperty("remote.execution", "false"));
    }

    /** WebSocket URL of the remote Playwright browser server. */
    public static String getRemoteServerUrl() {
        return getProperty("remote.server.url", "ws://localhost:8080/");
    }
}

