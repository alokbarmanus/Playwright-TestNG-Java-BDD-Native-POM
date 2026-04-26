
package com.automation.runners;

import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.FeatureWrapper;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG entry-point for the Cucumber BDD suite.
 *
 * <p>
 * Run all scenarios tagged {@code @regression}:
 * <pre>
 * mvn test -Dcucumber.filter.tags="@regression"
 * </pre>
 */


@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.automation.hooks", "com.automation.stepdefinitions"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @Override
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        super.runScenario(pickleWrapper, featureWrapper);
    }
}
