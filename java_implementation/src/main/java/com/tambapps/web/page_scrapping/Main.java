package com.tambapps.web.page_scrapping;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.tambapps.web.page_scrapping.parameters.Arguments;
import com.tambapps.web.page_scrapping.parameters.ScrapingType;
import com.tambapps.web.page_scrapping.printing.Printer;
import com.tambapps.web.page_scrapping.saver.ImageSaver;
import com.tambapps.web.page_scrapping.saver.LinkSaver;
import com.tambapps.web.page_scrapping.saver.Saver;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) {
    Printer.start();
    run(args);
    Printer.stop();
  }

  private static void run(String[] args) {
    Arguments arguments = new Arguments();

    JCommander jCommander = JCommander.newBuilder()
        .addObject(arguments)
        .build();
    try {
      jCommander.parse(args);
    } catch (ParameterException e) {
      Printer.log("Error: %s", e.getMessage());
      jCommander.usage();
      return;
    }

    Printer.setVerboseEnabled(arguments.isVerboseEnabled());
    File dir = arguments.getDirectory();
    Executor executor = Executors.newFixedThreadPool(arguments.getNbThreads());
    Set<Saver> savers = arguments.getTypes().stream()
        .map(t -> getSaver(t, executor, dir))
        .collect(Collectors.toSet());

    String typesUsed = arguments.getTypes().stream()
        .map(ScrapingType::name)
        .map(String::toLowerCase)
        .reduce((s1, s2) -> s1 + ", " + s2).get();

    Printer.log("About to save %s", typesUsed);
    for (String url : arguments.getUrls()) {
      Printer.log("\n");
      scrapUrl(url, savers);
    }

    Printer.log("\n");
    savers.forEach(Saver::printResult);
  }

  private static void scrapUrl(String url, Set<Saver> savers) {
    Printer.log("Processing url %s", url);
    Document doc;
    try {
      doc = Jsoup.connect(url).get();
    } catch (HttpStatusException e) {
      Printer.log("Error while connecting to url %s: Status Code: %d", url, e.getStatusCode());
      return;
    } catch (IOException e) {
      Printer.log("Error while connecting to url %s: %s", url, e.getMessage());
      return;
    }
    for (Element element : doc.select("*")) {
      for (Saver saver : savers) {
        saver.processElement(element);
      }
    }
  }

  private static Saver getSaver(ScrapingType type, Executor executor, File dir) {
    switch (type) {
      default:
      case LINKS:
        return new LinkSaver(executor, dir);
      case IMAGES:
        return new ImageSaver(executor, dir);
      case TEXT:
        throw new UnsupportedOperationException("Not implemented yet");
    }
  }

}
