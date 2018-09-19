package com.tambapps.web.page_scrapping.saver

import com.tambapps.web.page_scrapping.util.Printer
import groovy.util.slurpersupport.NodeChild

import java.nio.file.Files
import java.util.concurrent.Callable
import java.util.concurrent.Executor

class ImageSaver extends AbstractSaver {

    private final Map<Integer, Integer> errorsMap = new HashMap<>()

    ImageSaver(Executor executor, File dir) {
        super(executor, dir, 'img')
    }

    @Override
    boolean process(NodeChild element) {
        executorService.submit(createTask(element))
        return true
    }

    protected Callable<Integer> createTask(NodeChild element) {
        return {
            int returnCode = 0
            String link = element.attributes().get('src')
            URL url
            try {
                url = new URL(link)
            } catch (MalformedURLException e) {
                returnCode = 1
                errorsMap.put(returnCode, errorsMap.getOrDefault(returnCode, 0) + 1)
                return returnCode
            }
            File file = getAvailableFile(getFileName(url))
            try {
                Files.copy(url.openStream(), file.toPath())
            } catch (IOException e) {
                returnCode = 2
                errorsMap.put(returnCode, errorsMap.getOrDefault(returnCode, 0) + 1)
                return returnCode
            }
            return returnCode
        }
    }

    private static String getFileName(URL url) {
        String file = url.getFile()
        return file.substring(file.lastIndexOf('/') + 1)
    }

    @Override
    void printResult() {
        Printer.print("$treatedCount images were treated")
        if (errorsMap.keySet().isEmpty()) {
            Printer.print('no error were encountered')
        } else {
            for (Integer errorCode : errorsMap.keySet()) {
                printError(errorCode)
            }
        }
    }

    private void printError(int errorCode) {
        int count = errorsMap.getOrDefault(errorCode, 0)
        String error
        switch (errorCode) {
            case 1:
                error = 'malformed url error'
                break
            case 2:
                error = 'error while writing image'
        }
        if (count > 0) {
            Printer.print("$count images couldn't be treated due to an $error")
        }
    }

}
