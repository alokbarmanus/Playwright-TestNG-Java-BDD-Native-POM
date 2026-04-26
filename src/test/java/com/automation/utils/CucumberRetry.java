package com.automation.utils;

import io.cucumber.java.Scenario;
import io.cucumber.java.Status;
import io.cucumber.java.After;
import java.util.HashMap;
import java.util.Map;

public class CucumberRetry {
    private static final int MAX_RETRY_COUNT;
    private static final Map<String, Integer> retryCounts = new HashMap<>();

    static {
        String sysProp = System.getProperty("test.retry.count");
        int count = 1;
        if (sysProp != null) {
            try {
                count = Integer.parseInt(sysProp);
            } catch (NumberFormatException ignored) {}
        }
        MAX_RETRY_COUNT = count;
    }

    @After
    public void retryFailedScenario(Scenario scenario) {
        if (scenario.getStatus() == Status.FAILED) {
            String scenarioId = scenario.getId();
            int currentRetry = retryCounts.getOrDefault(scenarioId, 0);
            if (currentRetry < MAX_RETRY_COUNT) {
                retryCounts.put(scenarioId, currentRetry + 1);
                System.out.println("[CucumberRetry] Retrying scenario: " + scenario.getName() + " (Attempt " + (currentRetry + 2) + "/" + (MAX_RETRY_COUNT + 1) + ")");
                throw new RetryScenarioException();
            }
        }
    }

    public static class RetryScenarioException extends RuntimeException {
        public RetryScenarioException() {
            super("Retrying scenario as per retry logic");
        }
    }
}
