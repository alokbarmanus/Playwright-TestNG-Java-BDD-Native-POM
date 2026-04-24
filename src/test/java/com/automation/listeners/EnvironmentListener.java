package com.automation.listeners;

import org.testng.IExecutionListener;

public class EnvironmentListener implements IExecutionListener {
    @Override
    public void onExecutionStart() {
        // Set up environment variables or configuration here if needed
        System.out.println("[EnvironmentListener] Test execution started.");
    }

    @Override
    public void onExecutionFinish() {
        // Clean up environment variables or configuration here if needed
        System.out.println("[EnvironmentListener] Test execution finished.");
    }
}
