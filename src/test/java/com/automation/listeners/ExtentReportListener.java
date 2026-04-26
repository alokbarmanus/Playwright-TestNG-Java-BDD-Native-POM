package com.automation.listeners;

import com.automation.utils.ExtentManager;
import org.testng.IExecutionListener;

public class ExtentReportListener implements IExecutionListener {
    @Override
    public void onExecutionStart() {
        // Initialize ExtentReports before any tests run
        ExtentManager.init("target/extent-reports/ExtentReport.html");
    }

    @Override
    public void onExecutionFinish() {
        // Flush ExtentReports after all tests are finished
        ExtentManager.flush();
    }
}
