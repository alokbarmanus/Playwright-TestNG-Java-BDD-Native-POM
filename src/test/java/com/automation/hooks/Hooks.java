package com.automation.hooks;

import com.automation.utils.ConfigReader;
import com.automation.utils.JsonDataReader;
import com.automation.utils.PlaywrightManager;
import com.automation.utils.ExtentManager;
import com.automation.utils.ScenarioContext;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Map;

/**
 * Cucumber Hooks — manages Playwright lifecycle and test-data loading for each
 * scenario.
 *
 * <ul>
 * <li>{@code @Before} — resolves any {@code @dataFile:} tag, loads JSON test
 * data into {@link ScenarioContext}, then initialises Playwright.</li>
 * <li>{@code @After} — captures a screenshot on failure, then tears everything
 * down.</li>
 * </ul>
 *
 * <h3>@dataFile tag convention</h3>
 * 
 * <pre>
 * &#64;dataFile:env/&lt;env&gt;/data/someData.json
 * </pre>
 * 
 * The path prefix {@code env/} maps to the classpath folder
 * {@code environments/}. {@code ${env}} in the path is replaced with the active
 * environment name from {@link ConfigReader#getActiveEnv()}.
 */
public class Hooks {

	private final ScenarioContext scenarioContext;

	public Hooks(ScenarioContext scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	/**
	 * Runs before every scenario. Loads JSON test data (if {@code @dataFile:} tag
	 * present), then spins up Playwright.
	 */
	@Before
	public void setUp(Scenario scenario) {
		// Create a test node for this scenario
		ExtentManager.createTest(scenario.getName());
		// Log scenario name as description for TestNG emailable-report.html
		// Reporter.log("Scenario: " + scenario.getName(), true);
		System.out.println("\n========================================");
		System.out.println("SCENARIO STARTED: " + scenario.getName());
		System.out.println("Tags          : " + scenario.getSourceTagNames());
		System.out.println("========================================");

		loadJsonDataIfTagged(scenario);

		PlaywrightManager.initPlaywright();
	}

	/**
	 * Runs after every scenario. Attaches a full-page screenshot if the scenario
	 * failed, then closes all Playwright resources.
	 */
		@After
		public void tearDown(Scenario scenario) {
			try {
				if (scenario.isFailed()) {
					Page page = PlaywrightManager.getPage();
					byte[] screenshot = null;
					String screenshotDir = "target/extent-reports/img";
					Files.createDirectories(Paths.get(screenshotDir)); // Ensure directory exists
					String screenshotPath = screenshotDir + "/FAILURE_SCREENSHOT_" + System.currentTimeMillis() + ".png";
					if (page != null) {
						screenshot = page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
						scenario.attach(screenshot, "image/png", "FAILURE_SCREENSHOT_" + scenario.getName());
						System.out.println("Screenshot attached for failed scenario: " + scenario.getName());
					}
					// Log failure and attach screenshot to ExtentReports
					if (screenshot != null) {
						ExtentManager.getTest().fail("Scenario failed. Screenshot attached.").addScreenCaptureFromPath(
								"../extent-reports/img/" + new java.io.File(screenshotPath).getName());
					} else {
						ExtentManager.getTest().fail("Scenario failed.");
					}
				} else {
					ExtentManager.getTest().pass("Scenario passed.");
				}
			} catch (Exception ex) {
				ExtentManager.getTest().warning("Failed to capture or save screenshot: " + ex.getMessage());
			} finally {
				PlaywrightManager.closePlaywright();
				System.out.println("========================================");
				System.out.printf("SCENARIO FINISHED: %-40s [%s]%n", scenario.getName(), scenario.getStatus());
				System.out.println("========================================\n");
			}
	}

	// -------------------------------------------------------------------------
	// Internal helpers
	// -------------------------------------------------------------------------

	/**
	 * Scans the scenario tags for one starting with {@code @dataFile:}, resolves
	 * the path, and loads the JSON data into {@link ScenarioContext}.
	 *
	 * <p>
	 * Path resolution rules:
	 * <ul>
	 * <li>{@code ${env}} → replaced with the active environment name (e.g.
	 * {@code dev}, {@code sit}, {@code uat})</li>
	 * <li>Leading {@code env/} → replaced with {@code environments/} to match the
	 * classpath resource folder</li>
	 * </ul>
	 */
	private void loadJsonDataIfTagged(Scenario scenario) {
		scenario.getSourceTagNames().stream().filter(tag -> tag.startsWith("@dataFile:")).findFirst().ifPresent(tag -> {
			String rawPath = tag.substring("@dataFile:".length());

			// Resolve ${env} placeholder
			String resolvedPath = rawPath.replace("${env}", ConfigReader.getActiveEnv());

			// Map the short "env/" prefix to the classpath folder name
			if (resolvedPath.startsWith("env/")) {
				resolvedPath = "environments/" + resolvedPath.substring("env/".length());
			}

			System.out.println("[Hooks] Loading test data from: " + resolvedPath);
			Map<String, String> testData = JsonDataReader.load(resolvedPath);
			scenarioContext.putAll(testData);
			System.out.println("[Hooks] Flat test data loaded: " + testData);

			Map<String, Map<String, String>> nestedData = JsonDataReader.loadNestedMap(resolvedPath);
			if (!nestedData.isEmpty()) {
				scenarioContext.putNestedAll(nestedData);
				System.out.println("[Hooks] Nested test data loaded: " + nestedData.keySet());
			}
		});
	}
}
