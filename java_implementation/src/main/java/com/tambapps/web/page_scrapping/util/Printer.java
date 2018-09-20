package com.tambapps.web.page_scrapping.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;

public class Printer {

  private static final BlockingQueue<String> LOGS_QUEUE = new LinkedBlockingDeque<>();
  private static boolean verboseEnabled;

  public static void print(String s, Object... args) {
    LOGS_QUEUE.add(String.format(s, args));
  }

  public static void verbose(String s, Object... args) {
    if (verboseEnabled) {
      print(s, args);
    }
  }

  public static void newLine() {
    print("");
  }

  public static void start(Executor executor, boolean verboseEnabled) {
    Printer.verboseEnabled = verboseEnabled;
    executor.execute(() -> {
      while (true) {
        try {
          System.out.println(LOGS_QUEUE.take());
        } catch (InterruptedException e) {
          return;
        }
      }
    });
  }

}
