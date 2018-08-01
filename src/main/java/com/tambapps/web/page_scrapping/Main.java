package com.tambapps.web.page_scrapping;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.tambapps.web.page_scrapping.parameters.Arguments;
import com.tambapps.web.page_scrapping.parameters.ScrappingType;
import com.tambapps.web.page_scrapping.printing.Logger;
import com.tambapps.web.page_scrapping.saver.UrlImagesSaver;
import com.tambapps.web.page_scrapping.util.LazyExecutorSupplier;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) {
    Logger.start();
    run(args);
    Logger.stop();
  }

  private static void run(String[] args) {
    Arguments arguments = new Arguments();

    JCommander jCommander = JCommander.newBuilder()
        .addObject(arguments)
        .build();
    try {
      jCommander.parse(args);
    } catch (ParameterException e) {
      Logger.log("Error: %s", e.getMessage());
      jCommander.usage();
      return;
    }

    Logger.setVerboseEnabled(arguments.isVerboseEnabled());

    File directory = new File(arguments.getDirectory());
    if (!directory.exists() && !directory.mkdir()) {
      Logger.log("Error: Couldn't create directory");
      return;
    }
    if (!directory.isDirectory()) {
      Logger.log("This isn't a directory: %s", arguments.getDirectory());
      return;
    }

    LazyExecutorSupplier lazyExecutorSupplier = new LazyExecutorSupplier(arguments.getNbThreads());

    for (String url : arguments.getUrls()) {
      scrapUrl(lazyExecutorSupplier, url, arguments.getType(), directory);
    }

    if (lazyExecutorSupplier.wasSupplied()) {
      lazyExecutorSupplier.get().shutdown();
    }
  }

  private static void scrapUrl(Supplier<ExecutorService> executorSupplier, String url,
      ScrappingType type, File root) {
    Document doc;
    try {
      doc = Jsoup.connect(url).get();
    } catch (HttpStatusException e) {
      Logger.log("Error while connecting to url %s: Status Code: %d", url, e.getStatusCode());
      return;
    } catch (IOException e) {
      Logger.log("Error while connecting to url %s: %s", url, e.getMessage());
      return;
    }
    switch (type) {
      case LINKS:
        saveLinks(doc, root);
        break;
      case IMAGES:
        saveImages(executorSupplier, doc, root);
        break;
      case BOTH:
        saveLinks(doc, root);
        saveImages(executorSupplier, doc, root);
        break;
    }
  }

  private static void saveLinks(Document doc, File root) {
    Elements linkElements = doc.select("a[href]");
    List<String> links = linkElements.stream()
        .map(e -> e.attr("abs:href"))
        .collect(Collectors.toList());
    try (FileWriter fileWriter = new FileWriter(new File(root, "links.txt"), true)) {
      boolean lineError = false;
      for (String link : links) {
        try {
          fileWriter.write(link + "\n");
        } catch (Exception e) {
          lineError = true;
        }
      }
      if (lineError) {
        Logger.log("Some links couldn't be written");
      }
    } catch (IOException e) {
      Logger.log("An error occured wile attempting to write: %s", e.getMessage());
    }
    Logger.log("%d links were treated", links.size());
  }

  private static void saveImages(Supplier<ExecutorService> executorSupplier, Document doc, File root) {
    Elements media = doc.select("[src]");
    List<URL> imagesLinks = media.stream()
        .filter(e -> e.tagName().equals("img"))
        .map(e -> {
          String url = e.attr("abs:src");
          try {
            return new URL(url);
          } catch (MalformedURLException exception) {
            Logger.log("Couldn't resolve url %s", url);
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());


    UrlImagesSaver imagesSaver = new UrlImagesSaver(executorSupplier.get(), root);
    Map<Integer, Integer> result = imagesSaver.saveImages(imagesLinks);

    int total = result.getOrDefault(UrlImagesSaver.OK, 0)
        + result.getOrDefault(UrlImagesSaver.FILE_CREATION_ERROR, 0)
        + result.getOrDefault(UrlImagesSaver.SAVING_ERROR, 0);

    if (Objects.equals(total, result.get(UrlImagesSaver.OK))) {
      Logger.log("All images were saved successfully (%d images)", total);
    } else {
      Logger.log("Out of %d images:", imagesLinks.size());
      printNonNullResult("%d images were saved successfully",
          result.getOrDefault(UrlImagesSaver.OK, 0));
      printNonNullResult("%d images were not saved due to a file creation error",
          result.getOrDefault(UrlImagesSaver.FILE_CREATION_ERROR, 0));
      printNonNullResult("%d images were not saved due to an error while saving",
          result.getOrDefault(UrlImagesSaver.SAVING_ERROR, 0));
    }
  }

  private static void printNonNullResult(String message, int result) {
    if (result > 0) {
      Logger.log(message, result);
    }
  }

}
