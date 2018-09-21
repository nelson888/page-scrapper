package com.tambapps.web.page_scrapping.saver

import groovy.transform.PackageScope

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorCompletionService

@PackageScope
abstract class AbstractSaver implements Saver {
    final ExecutorCompletionService<Integer> executorService
    private final String tag
    private final File dir
    private int treatedCount

    AbstractSaver(Executor executor, File dir, String tag) {
        this.tag = tag
        this.dir = dir
        treatedCount = 0
        executorService = new ExecutorCompletionService<>(executor)
    }

    void processElement(def element) {
        if (ANY.is(tag) || tag.equalsIgnoreCase(element.tagName())) {
            if (process(element)) {
                treatedCount++
            }
        }
    }

    protected abstract boolean process(def element)

    protected File getAvailableFile(String name) {
        File file = new File(dir, name)
        for (int i = 0; file.exists(); i++) {
            String number = new StringBuilder().with {
                append(i)
                while (length() < 3) {
                    insert(0, '0')
                }
                it.toString()
            }
            String fileName
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

    boolean isValidLink(String link) {
        return link && ['http', 'www'].any({ link.startsWith(it) })
    }
}
