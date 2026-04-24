package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import com.automation.utils.ExtentManager;

/**
 * TestNG entry-point for the Cucumber BDD suite.
 *
 * <p>Run all scenarios tagged {@code @regression}:
 * <pre>mvn test -Dcucumber.filter.tags="@regression"</pre>
 *
 * <p>Run only smoke scenarios:
 * <pre>mvn test -Dcucumber.filter.tags="@smoke"</pre>
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue     = {
                "com.automation.hooks",
                "com.automation.stepdefinitions"
        },
        plugin   = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml"
        },
        tags      = "@regression",
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

        @BeforeSuite(alwaysRun = true)
        public void beforeSuite() {
                // Initialize ExtentReports ONCE per test run
                ExtentManager.init("target/cucumber-reports/ExtentReport.html");
        }

        @AfterSuite(alwaysRun = true)
        public void afterSuite() {
                // Flush ExtentReports ONCE after all tests
                ExtentManager.flush();
        }

    /**
     * Override to control parallel execution at scenario level.
     * Set {@code parallel = true} and increase {@code thread-count} in testng.xml
     * to enable parallel runs — PlaywrightManager uses ThreadLocal so it is safe.
     */
        @Override
        @DataProvider(parallel = false)
        public Object[][] scenarios() {
                return super.scenarios();
        }
}
