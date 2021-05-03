package com.example.testkafka;

import com.intuit.karate.Results;
import com.intuit.karate.Runner.Builder;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class TestRunner {

  public static void main(String[] args) {
    JUnitCore.main(TestRunner.class.getCanonicalName());
  }

  @Test
  public void testWithReport() {
    Results result = new Builder()
        .forClass(TestRunner.class)
        .parallel(5);
    Assert.assertEquals(result.getErrorMessages(), 0, result.getFailCount());
  }

  @Test
  public void testConsumerSeparatelyWithSeparateReport() {
    Results result = new Builder()
        .path("classpath:features")

        // generate a separate report from other test-runner instance
        .reportDir("target" + File.separator + "karate" + File.separator + "Consumer")

        .forClass(TestRunner.class)
        .parallel(1);
    Assert.assertEquals(result.getErrorMessages(), 0, result.getFailCount());
  }
}