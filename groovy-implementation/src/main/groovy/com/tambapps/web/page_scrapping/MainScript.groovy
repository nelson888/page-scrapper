package com.tambapps.web.page_scrapping

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.tambapps.web.page_scrapping.saver.ImageSaver
import com.tambapps.web.page_scrapping.saver.LinkSaver
import com.tambapps.web.page_scrapping.saver.Saver
import com.tambapps.web.page_scrapping.parameter.Args
import com.tambapps.web.page_scrapping.parameter.ScrapingType
import com.tambapps.web.page_scrapping.saver.TextSaver
import com.tambapps.web.page_scrapping.util.Printer

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

String typesUsed = arguments.types.stream().map({ it.name().toLowerCase() }).reduce({
    s1, s2 -> s1 + ', ' + s2
}).get()
Printer.print("About to save $typesUsed")

for (String url : arguments.urls) {
    Printer.newLine()
    scrapUrl(url, savers)
}

savers.each({
    Printer.newLine()
    it.finish()
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
            return new TextSaver(executor, dir)
    }
}

@Grab('org.jsoup:jsoup:1.11.3')
void scrapUrl(String url, Set<Saver> savers) {
    Printer.print("Processing url $url")

    def document
    try {
        document = org.jsoup.Jsoup.connect(url).get()
    } catch (org.jsoup.HttpStatusException e) {
        Printer.print("Error while connecting to url: Status Code: $e.statusCode")
        return
    } catch (IOException | IllegalArgumentException e) {
        Printer.print("Error while accessing url: $e.message")
        return
    }

    for (def element : document.select("*")) {
        savers.forEach {
            it.processElement(element)
        }
    }
}
