package com.tambapps.web.page_scrapping;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.tambapps.web.page_scrapping.parameters.Arguments;
import com.tambapps.web.page_scrapping.parameters.ScrapingType;
import com.tambapps.web.page_scrapping.saver.ImageSaver;
import com.tambapps.web.page_scrapping.saver.LinkSaver;
import com.tambapps.web.page_scrapping.saver.Outcome;
import com.tambapps.web.page_scrapping.saver.Saver;

import com.tambapps.web.page_scrapping.saver.TextSaver;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Arguments arguments = new Arguments();

    JCommander jCommander = JCommander.newBuilder()
        .addObject(arguments)
        .build();
    try {
      jCommander.parse(args);
    } catch (ParameterException e) {
      LOGGER.error("Couldn't parse arguments: {}", e.getMessage());
      jCommander.usage();
      return;
    }
    ExecutorService executor = Executors.newFixedThreadPool(arguments.getNbThreads());
    File dir = arguments.getDirectory();

    Set<Saver> savers = arguments.getTypes().stream()
        .map(t -> getSaver(t, dir))
        .collect(Collectors.toSet());

    String typesUsed = arguments.getTypes().stream()
        .map(ScrapingType::name)
        .map(String::toLowerCase)
        .reduce((s1, s2) -> s1 + ", " + s2).get();

    LOGGER.info("Saving {}", typesUsed);
    for (String url : arguments.getUrls()) {
      scrapUrl(executor, url, savers);
    }

    savers.forEach(Saver::printResult);
    executor.shutdown();
  }

  private static void scrapUrl(ExecutorService executor, String url, Set<Saver> savers) {
    LOGGER.info("Processing url {}", url);
    try {
      Document doc = Jsoup.connect(url).get();
      processDocument(doc, executor, savers);
    } catch (HttpStatusException e) {
      LOGGER.error("Error while connecting to url {}: Status Code: {}", url, e.getStatusCode(), e);
    } catch (IOException | IllegalArgumentException e) {
      LOGGER.error("Error while connecting to url {}: {}", url, e.getMessage(), e);
    }
  }

  private static void processDocument(Document document, ExecutorService executor,
      Set<Saver> savers) {
    List<Future<Outcome>> futures = new ArrayList<>();

    for (Element element : document.select("*")) {
      for (Saver saver : savers) {
        saver.process(executor, element).ifPresent(futures::add);
      }
    }

    waitFutures(futures);
  }

  private static Saver getSaver(ScrapingType type, File dir) {
    switch (type) {
      default:
      case LINKS:
        return new LinkSaver(dir);
      case IMAGES:
        return new ImageSaver(dir);
      case TEXT:
        return new TextSaver(dir);
    }
  }

  private static void waitFutures(List<Future<Outcome>> futures) {
    for (Future future : futures) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.error("An error occurred while processing an element", e);
      }
    }
  }


}
