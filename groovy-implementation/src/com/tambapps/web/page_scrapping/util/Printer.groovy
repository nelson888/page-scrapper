package com.tambapps.web.page_scrapping.util

class Printer {

    private static boolean verbose
    private Printer() {}

    static void print(String message) {
        println(message)
    }

    static void verbose(String message) {
        if (verbose) {
            println(message)
        }
    }

    static void setVerbose(boolean v) {
        verbose = v
    }

}