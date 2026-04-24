package com.automation.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.automation.utils.ExtentManager;
import com.aventstack.extentreports.Status;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ExtentReportListener implements ITestListener {
    @Override
    public void onStart(ITestContext context) {
        ExtentManager.init("target/cucumber-reports/ExtentReport.html");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentManager.createTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentManager.getTest().log(Status.FAIL, result.getThrowable());
        // Attach screenshot if available
        String screenshotPath = System.getProperty("user.dir") + "/target/screenshots/" + result.getMethod().getMethodName() + ".png";
        File screenshot = new File(screenshotPath);
        if (screenshot.exists()) {
            try {
                byte[] fileContent = Files.readAllBytes(Paths.get(screenshotPath));
                String base64 = Base64.getEncoder().encodeToString(fileContent);
                ExtentManager.getTest().addScreenCaptureFromBase64String(base64, "Failure Screenshot");
            } catch (Exception e) {
                ExtentManager.getTest().log(Status.WARNING, "Could not attach screenshot: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.getTest().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }
}
