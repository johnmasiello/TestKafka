package com.example.testKafka.test.container;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/test_container",
    plugin = {"pretty",
        "json:target/cucumber-report-test-container.json",
        "html:target/cucumber-test-container.html"

    })
public class CucumberIntegrationTest {

}
