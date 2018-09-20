package com.tambapps.web.page_scrapping.saver

import com.tambapps.web.page_scrapping.util.Printer
import groovy.transform.PackageScope
import groovy.util.slurpersupport.NodeChild

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue

@PackageScope
abstract class FileSaver extends AbstractSaver {

    final BlockingQueue<String> textQueue = new LinkedBlockingQueue<>()
    private static final String END = "" //used to tell the worker thread that it must stop
    private final String fileName
    private int failCount = 0

    FileSaver(Executor executor, File dir, String tag, String name) {
        super(executor, dir, tag)

        File file = getAvailableFile(name)
        fileName = file.getName()
        if (!file.createNewFile()) {
            throw new RuntimeException('Couldn\'t create file')
        }
        executorService.submit({
            while (true) {
                String text
                try {
                    text = textQueue.take()
                } catch (InterruptedException e) {
                    return 1
                }
                if (END.is(text)) {
                    return 0
                }
                try {
                    file << (text + '\n')
                } catch (IOException e) {
                    Printer.verbose("Error while writing $text: $e.message")
                    failCount++
                }
            }
        })
    }

    @Override
    protected final boolean process(NodeChild element) {
        String text = processData(element)
        if (text) {
            textQueue.add(text)
            return true
        }
        return false
    }

    abstract String processData(NodeChild element)

    String getFileName() {
        return fileName
    }

    int getFailCount() {
        return failCount
    }

    @Override
    void finish() {
        textQueue.add(END)
        int returnCode = executorService.take().get()
        if (returnCode == 1) {
            Printer.print("The saving may not have finished because the process was interrupted")
        }
    }
}
