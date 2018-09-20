package com.tambapps.web.page_scrapping

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.tambapps.web.page_scrapping.saver.ImageSaver
import com.tambapps.web.page_scrapping.saver.LinkSaver
import com.tambapps.web.page_scrapping.saver.Saver
import com.tambapps.web.page_scrapping.parameter.Args
import com.tambapps.web.page_scrapping.parameter.ScrapingType
import com.tambapps.web.page_scrapping.util.Printer
import groovy.util.slurpersupport.NodeChild

@Grab(group = 'net.sourceforge.nekohtml', module = 'nekohtml', version = '1.9.14')
import org.cyberneko.html.parsers.SAXParser as HtmlParser

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

Args arguments = new Args()
JCommander jCommander = JCommander.newInstance(arguments)

try {
    jCommander.parse(args)
} catch (ParameterException e) {
    println e.message
    return
}
ExecutorService executor = Executors.newFixedThreadPool(arguments.nbThreads)
Printer.start(executor, arguments.verbose)
File dir = arguments.directory

Set<Saver> savers = arguments.types.collect { return getSaver(it, executor, dir) }.toSet()
XmlSlurper slurper = new XmlSlurper(new HtmlParser())
String typesUsed = arguments.types.stream().map({ it.name().toLowerCase() }).reduce({
    s1, s2 -> s1 + ', ' + s2
}).get()
Printer.print("About to save $typesUsed")
for (String url : arguments.urls) {
    Printer.newLine()
    scrapUrl(slurper, url, savers)
}

savers.each ({
    Printer.newLine()
    it.printResult()
})

executor.shutdownNow()

/*
 functions
 */
Saver getSaver(ScrapingType type, Executor executor, File dir) {
    switch (type) {
        case ScrapingType.LINKS:
            return new LinkSaver(executor, dir)
        case ScrapingType.IMAGES:
            return new ImageSaver(executor, dir)
        case ScrapingType.TEXT:
            throw new UnsupportedOperationException('Not implemented yet')
    }
}

void scrapUrl(XmlSlurper slurper, String url, Set<Saver> savers) {
    Printer.print("Processing url $url")

    def page
    try {
        page = slurper.parse(url)
    } catch(IOException e) {
        Printer.print("Error while accessing this url. Skipping it")
        return
    }

    for (NodeChild node : page.breadthFirst()) {
        savers.forEach {
            it.processElement(node)
        }
    }
}
