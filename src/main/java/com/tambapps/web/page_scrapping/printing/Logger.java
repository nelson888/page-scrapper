package com.tambapps.web.page_scrapping.printing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Logger {

  private static boolean verboseEnabled;
  private static final BlockingQueue<String> LOGS_QUEUE = new LinkedBlockingDeque<>();
  private static final Thread LOGGING_THREAD = new Thread(new Runnable() {
    private boolean interrupted = false;
    @Override
    public void run() {
      while (!interrupted) {
        try {
          System.out.println(LOGS_QUEUE.take());
        } catch (InterruptedException e) {
          interrupted = true;
        }
      }
    }
  });

  public static void log(String s, Object... args) {
    LOGS_QUEUE.add(String.format(s, args));
  }

  public static void verbose(String s, Object... args) {
    if (verboseEnabled) {
      log(s, args);
    }
  }

  public static void setVerboseEnabled(boolean verboseEnabled) {
    Logger.verboseEnabled = verboseEnabled;
  }

  public static void start() {
    LOGGING_THREAD.start();
  }

  public static void stop() {
    LOGGING_THREAD.interrupt();
  }

}
