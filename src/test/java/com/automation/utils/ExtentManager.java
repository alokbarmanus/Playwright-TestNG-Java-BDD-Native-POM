package com.automation.utils;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.TestNG;
import com.microsoft.playwright.Playwright;
import io.cucumber.testng.AbstractTestNGCucumberTests;

public class ExtentManager {
    private static ExtentReports extent;
    private static ExtentSparkReporter sparkReporter;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    /**
     * Initializes the ExtentReports instance. Should be called ONCE per test run
     * (not per scenario).
     * Thread-safe singleton initialization.
     * 
     * @param reportPath Path to the HTML report file.
     */
    public static synchronized void init(String reportPath) {
        if (extent == null) {
            sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Automation Test Report");
            sparkReporter.config().setReportName("BDD Playwright Test Report");
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // Add industry-standard system info
            extent.setSystemInfo("Project Name", "Playwright-TestNG-Java-BDD-Native-POM");
            extent.setSystemInfo("Project Version", "1.0-SNAPSHOT");
            extent.setSystemInfo("Test Suite", "BDD Playwright Test Suite");
            extent.setSystemInfo("Execution Start Time", java.time.LocalDateTime.now().toString());
            extent.setSystemInfo("Operating System", System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            String hostName = null;
            try {
                hostName = java.net.InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                hostName = "Unknown";
            }
            extent.setSystemInfo("Host Name", hostName);
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
            extent.setSystemInfo("Environment", com.automation.utils.ConfigReader.getActiveEnv());
            extent.setSystemInfo("Browser", com.automation.utils.ConfigReader.getBrowser());
            extent.setSystemInfo("Base URL", com.automation.utils.ConfigReader.getBaseUrl());
            extent.setSystemInfo("Headless", String.valueOf(com.automation.utils.ConfigReader.isHeadless()));
            // Dynamic version info
            String playwrightVersion = null;
            try {
                playwrightVersion = Playwright.class.getPackage().getImplementationVersion();
            } catch (Exception e) {
                playwrightVersion = "Unknown";
            }
            String testngVersion = null;
            try {
                testngVersion = TestNG.class.getPackage().getImplementationVersion();
            } catch (Exception e) {
                testngVersion = "Unknown";
            }
            String cucumberVersion = null;
            try {
                cucumberVersion = AbstractTestNGCucumberTests.class.getPackage().getImplementationVersion();
            } catch (Exception e) {
                cucumberVersion = "Unknown";
            }
            extent.setSystemInfo("Playwright Version", playwrightVersion);
            extent.setSystemInfo("Cucumber Version", cucumberVersion);
            extent.setSystemInfo("TestNG Version", testngVersion);
            extent.setSystemInfo("Remote Execution", String.valueOf(com.automation.utils.ConfigReader.isRemoteExecution()));
            extent.setSystemInfo("Remote Server URL", com.automation.utils.ConfigReader.getRemoteServerUrl());
            // Add more as needed (e.g., CI build info, Git info, device info, etc.)
        }
    }

    /**
     * Returns the singleton ExtentReports instance.
     * 
     * @return ExtentReports instance
     * @throws IllegalStateException if not initialized
     */
    public static ExtentReports getExtent() {
        if (extent == null) {
            throw new IllegalStateException(
                    "ExtentReports not initialized. Call ExtentManager.init() once before using.");
        }
        return extent;
    }

    /**
     * Creates a new ExtentTest for the current thread.
     * 
     * @param testName Name of the test/scenario
     * @return ExtentTest instance
     * @throws IllegalStateException if ExtentReports is not initialized
     */
    public static ExtentTest createTest(String testName) {
        if (extent == null) {
            throw new IllegalStateException(
                    "ExtentReports not initialized. Call ExtentManager.init() before creating tests.");
        }
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    /**
     * Gets the ExtentTest for the current thread.
     * 
     * @return ExtentTest instance
     * @throws IllegalStateException if no test is set for this thread
     */
    public static ExtentTest getTest() {
        ExtentTest t = test.get();
        if (t == null) {
            throw new IllegalStateException("No ExtentTest found for current thread. Call createTest() first.");
        }
        return t;
    }

    /**
     * Flushes the ExtentReports instance. Should be called ONCE after all tests are
     * finished.
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void logInfo(String message) {
        getTest().log(Status.INFO, message);
    }

    public static void logPass(String message) {
        getTest().log(Status.PASS, message);
    }

    public static void logWarning(String message) {
        getTest().log(Status.WARNING, message);
    }

    public static void logFail(String message) {
        getTest().log(Status.FAIL, message);
    }
}
