package com.automation.utils;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;
import java.util.*;

public class CucumberRetryPlugin implements EventListener {
    private final int maxRetries;
    private final Map<String, Integer> retryCounts = new HashMap<>();

    public CucumberRetryPlugin() {
        // Read from system property or default to 1
        String retryProp = System.getProperty("test.retry.count", "1");
        this.maxRetries = Integer.parseInt(retryProp);
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
    }

    private void handleTestCaseFinished(TestCaseFinished event) {
        if (event.getResult().getStatus() == Status.FAILED) {
            String scenarioId = event.getTestCase().getId().toString();
            int retries = retryCounts.getOrDefault(scenarioId, 0);
            if (retries < maxRetries) {
                retryCounts.put(scenarioId, retries + 1);
                throw new RetryScenarioException();
            }
        }
    }

    // Custom unchecked exception to trigger retry
    public static class RetryScenarioException extends RuntimeException {}
}
