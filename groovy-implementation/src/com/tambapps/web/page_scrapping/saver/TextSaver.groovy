package com.tambapps.web.page_scrapping.saver

import com.tambapps.web.page_scrapping.util.Printer
import groovy.util.slurpersupport.NodeChild

import java.util.concurrent.Executor

class TextSaver extends FileSaver {

    TextSaver(Executor executor, File dir) {
        super(executor, dir, Saver.ANY, 'text.txt')
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

    @Override
    void printResult() {
        Printer.print("The texts were saved in $fileName")
        Printer.print("$treatedCount texts were treated")
        if (failCount > 0) {
            Printer.print("$failCount texts couldn't be saved")
        }
    }

}
