package com.example.testKafka.embedded;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/embedded",
    plugin = {"pretty",
        "json:target/cucumber-report-embedded.json",
        "html:target/cucumber-embedded.html"
    })
public class CucumberIntegrationTest {

}