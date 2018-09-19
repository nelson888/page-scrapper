package com.tambapps.web.page_scrapping.extractor

import groovy.util.slurpersupport.NodeChild

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorCompletionService

abstract class AbstractSaver implements Saver {
    private final String tag
    private final File dir
    private int treatedCount
    final ExecutorCompletionService<Integer> executorService

    AbstractSaver(Executor executor, File dir, String tag) {
        this.tag = tag
        this.dir = dir
        treatedCount = 0
        executorService = new ExecutorCompletionService<>(executor)
    }

    void processElement(NodeChild element) {
        if (tag.equalsIgnoreCase(element.name())) {
            if (process(element)) {
                treatedCount++
            }
        }
    }

    protected abstract boolean process(NodeChild element)

    protected File getAvailableFile(String name) {
        File file = new File(dir, name)
        for (int i = 0; file.exists(); i++) {
            StringBuilder number = new StringBuilder(String.valueOf(i))
            while (number.length() < 3) { //max = 999
                number.insert(0, '0')
            }
            String fileName;
            if (name.contains(".")) {
                int dotIndex = name.indexOf('.')
                fileName = name.substring(0, dotIndex) + '_' + number + name.substring(dotIndex)
            } else {
                fileName = name + '_' + number
            }
            file = new File(dir, fileName)
        }
        return file
    }

    protected int getTreatedCount() {
        return treatedCount
    }
}
