package com.tambapps.web.page_scrapping.saver

import com.tambapps.web.page_scrapping.util.Printer
import groovy.util.slurpersupport.NodeChild

import java.util.concurrent.Executor

class TextSaver extends FileSaver {

    TextSaver(Executor executor, File dir) {
        super(executor, dir, Saver.ANY, 'text.txt', 'text')
    }

    @Override
    String processData(NodeChild element) {
        String text = element.text()?.trim()
        if (!text) {
            return null
        }
        Printer.verbose("Found text  $text")
        String tagName = element.name()
        return "$tagName: ``` $text ```"
    }

}
