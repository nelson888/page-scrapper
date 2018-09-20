package com.tambapps.web.page_scrapping.saver

import com.tambapps.web.page_scrapping.util.Printer
import groovy.util.slurpersupport.NodeChild

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue

class LinkSaver extends AbstractSaver {

    private final BlockingQueue<String> linksQueue = new LinkedBlockingQueue<>()
    private final String fileName
    private int failCount = 0

    LinkSaver(Executor executor, File dir) {
        super(executor, dir, 'a')
        File file = getAvailableFile('links.txt')
        fileName = file.getName()
        if (!file.createNewFile()) {
            throw new RuntimeException('Couldn\'t create file')
        }
        executorService.submit({
            while (true) {
                String link
                try {
                    link = linksQueue.take()
                } catch (InterruptedException e) {
                    return 0
                }
                try {
                    file << (link + '\n')
                } catch (IOException e) {
                    Printer.verbose("Error while writing link $link: $e.message")
                    failCount++
                }
            }
        })
    }

    @Override
    boolean process(NodeChild element) {
        String link = (String) element.attributes().get('href')
        if (!link.trim().isEmpty() && !link.startsWith('#')) {
            linksQueue.add(link)
            Printer.print("Found link $link")
            return true
        }
        return false
    }

    @Override
    void printResult() {
        Printer.print("The links were saved in $fileName")
        Printer.print("$treatedCount links were treated")
        Printer.print("$treatedCount links were treated");
        if (failCount > 0) {
            Printer.print("$failCount links couldn't be saved");
        }
    }
}
