package com.tambapps.web.page_scrapping.util

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingDeque

class Printer {

    private static final BlockingQueue<String> LOGS_QUEUE = new LinkedBlockingDeque<>()
    private static boolean verbose = false

    private Printer() {}

    static void print(String message) {
        LOGS_QUEUE.add(message)
    }

    static void newLine() {
        print("")
    }

    static void verbose(String message) {
        if (verbose) {
            print(message)
        }
    }

    static void start(Executor executor, boolean verbose) {
        Printer.verbose = verbose
        executor.execute({
            while (true) {
                try {
                    println(LOGS_QUEUE.take())
                } catch (InterruptedException e) {
                    return
                }
            }
        })
    }

}