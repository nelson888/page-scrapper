package com.tambapps.web.page_scrapping.printing;

public class Printer {

  private static boolean verboseEnabled;

  public static void print(String s, Object... args) {
    System.out.format(s, args).println();
  }

  public static void verbose(String s, Object... args) {
    if (verboseEnabled) {
      print(s, args);
    }
  }

  public static void setVerboseEnabled(boolean verboseEnabled) {
    Printer.verboseEnabled = verboseEnabled;
  }

}
