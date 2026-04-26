package com.automation.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

public class ScenarioNameListener_backup implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        // If scenario name is passed as a parameter, set it as description
        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            String scenarioName = params[0].toString();
            result.setAttribute("description", scenarioName);
            Reporter.log("Scenario: " + scenarioName, true);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {}
    @Override
    public void onTestFailure(ITestResult result) {}
    @Override
    public void onTestSkipped(ITestResult result) {}
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override
    public void onStart(ITestContext context) {}
    @Override
    public void onFinish(ITestContext context) {}
}
