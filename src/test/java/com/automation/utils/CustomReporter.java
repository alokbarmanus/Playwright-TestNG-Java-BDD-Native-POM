package com.automation.utils;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;
import java.util.List;
import java.io.*;

public class CustomReporter implements IReporter {

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            File file = new File(outputDirectory + File.separator + "custom-cucumber-report.html");
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println("<html><head><title>Cucumber Scenario Report</title></head><body>");
            writer.println("<h1>Cucumber Scenario Execution Report</h1>");
            writer.println("<table border='1'><tr><th>Suite</th><th>Test</th><th>Scenario Name</th><th>Status</th></tr>");

            for (ISuite suite : suites) {
                for (ISuiteResult suiteResult : suite.getResults().values()) {
                    ITestContext context = suiteResult.getTestContext();
                    for (ITestResult tr : context.getPassedTests().getAllResults()) {
                        writer.println("<tr><td>" + suite.getName() + "</td><td>" + context.getName() + "</td><td>" +
                                getScenarioName(tr) + "</td><td>PASS</td></tr>");
                    }
                    for (ITestResult tr : context.getFailedTests().getAllResults()) {
                        writer.println("<tr><td>" + suite.getName() + "</td><td>" + context.getName() + "</td><td>" +
                                getScenarioName(tr) + "</td><td>FAIL</td></tr>");
                    }
                    for (ITestResult tr : context.getSkippedTests().getAllResults()) {
                        writer.println("<tr><td>" + suite.getName() + "</td><td>" + context.getName() + "</td><td>" +
                                getScenarioName(tr) + "</td><td>SKIPPED</td></tr>");
                    }
                }
            }
            writer.println("</table></body></html>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getScenarioName(ITestResult tr) {
        // Cucumber scenario name is usually the first parameter
        Object[] params = tr.getParameters();
        if (params != null && params.length > 0) {
            return params[0].toString();
        }
        return tr.getName();
    }
}