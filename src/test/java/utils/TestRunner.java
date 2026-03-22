package utils;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions"},
        plugin = {"junit:target/cucumber-reports/Cucumber.xml", "json:target/cucumber-reports/Cucumber.json"},
        monochrome = true,
        tags = "@Test")
public class TestRunner {
}
