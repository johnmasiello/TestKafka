package com.example.testkafka.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
  private static final Logger LOG = LoggerFactory.getLogger(Util.class);

  public static int magicNumber() {
    LOG.info("Computing a magic number...");
    return 3;
  }

  public static void waitFor(long millis, String countdownMessage) {
    for (int i = 0; i < millis / 1000; i++) {
      try {
        LOG.info("{}  --  {} seconds ...", countdownMessage, i + 1);
        Thread.sleep(1000);

      } catch (InterruptedException e) {
        LOG.error("Sleep interrupted", e);
      }
    }
    try {
      Thread.sleep(millis % 1000);

    } catch (InterruptedException e) {
      LOG.error("Sleep interrupted", e);
    }
  }
}
