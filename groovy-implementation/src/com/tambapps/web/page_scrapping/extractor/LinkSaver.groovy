package com.tambapps.web.page_scrapping.extractor

import com.tambapps.web.page_scrapping.util.Printer
import groovy.util.slurpersupport.NodeChild

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue

class LinkSaver extends AbstractSaver {

    private final File file
    private final BlockingQueue<String> linksQueue = new LinkedBlockingQueue<>()
    private int nbLinks

    LinkSaver(Executor executor, File dir) {
        super(executor, dir, 'a')
        nbLinks = 0
        file = getAvailableFile('links.txt')
        if (!file.createNewFile()) {
            throw new RuntimeException('Couldn\'t create file')
        }
        executorService.submit({
            boolean running = true
            while (running) {
                try {
                    String link = linksQueue.take()
                    file << (link + '\n')
                } catch(InterruptedException e) {
                    running = false
                }
            }
            return 0
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
        Printer.print("$treatedCount links were treated")
    }
}
