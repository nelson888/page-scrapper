package com.tambapps.web.page_scrapping.saver

import com.tambapps.web.page_scrapping.util.Printer
import groovy.util.slurpersupport.NodeChild

import java.util.concurrent.Executor

class LinkSaver extends FileSaver {

    LinkSaver(Executor executor, File dir) {
        super(executor, dir, 'a', 'links.txt', 'link')
    }

    @Override
    String processData(NodeChild element) {
        String link = ((String) element.attributes().get('href'))?.trim()
        if (link && ['http', 'www'].any({ link.startsWith(it) })) {
            Printer.verbose("Found link $link")
            return link
        }
        return null
    }

}
