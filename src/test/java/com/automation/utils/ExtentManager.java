package com.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    private static ExtentReports extent;
    private static ExtentHtmlReporter htmlReporter;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    /**
     * Initializes the ExtentReports instance. Should be called ONCE per test run (not per scenario).
     * Thread-safe singleton initialization.
     * @param reportPath Path to the HTML report file.
     */
    public static synchronized void init(String reportPath) {
        if (extent == null) {
            htmlReporter = new ExtentHtmlReporter(reportPath);
            htmlReporter.config().setTheme(Theme.STANDARD);
            htmlReporter.config().setDocumentTitle("Automation Test Report");
            htmlReporter.config().setReportName("BDD Playwright Test Report");
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
        }
    }

    /**
     * Returns the singleton ExtentReports instance.
     * @return ExtentReports instance
     * @throws IllegalStateException if not initialized
     */
    public static ExtentReports getExtent() {
        if (extent == null) {
            throw new IllegalStateException("ExtentReports not initialized. Call ExtentManager.init() once before using.");
        }
        return extent;
    }

    /**
     * Creates a new ExtentTest for the current thread.
     * @param testName Name of the test/scenario
     * @return ExtentTest instance
     * @throws IllegalStateException if ExtentReports is not initialized
     */
    public static ExtentTest createTest(String testName) {
        if (extent == null) {
            throw new IllegalStateException("ExtentReports not initialized. Call ExtentManager.init() before creating tests.");
        }
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    /**
     * Gets the ExtentTest for the current thread.
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
     * Flushes the ExtentReports instance. Should be called ONCE after all tests are finished.
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
