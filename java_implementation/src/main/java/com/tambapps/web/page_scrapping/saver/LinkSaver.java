package com.tambapps.web.page_scrapping.saver;

import com.tambapps.web.page_scrapping.printing.Printer;

import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class LinkSaver extends AbstractSaver {
  private final BlockingQueue<String> linksQueue = new LinkedBlockingQueue<>();
  private final String fileName;
  private int failCount = 0;

  public LinkSaver(Executor executor, File dir) {
    super(executor, dir, "a");
    File file = getAvailableFile("links.txt");

    try {
      if (!file.createNewFile()) {
        throw new RuntimeException("Couldn\'t create file");
      }
    } catch (IOException e) {
      throw new RuntimeException("Couldn\'t create file");
    }
    fileName = file.getName();

    FileWriter writer;
    try {
      writer = new FileWriter(file);
    } catch (IOException e) {
      throw new RuntimeException("Couldn\'t instanciate file writer");
    }

    executorService.submit(() -> {
      boolean running = true;
      while (running) {
        try {
          String link = linksQueue.take();
          writer.write(link + "\n");
        } catch (InterruptedException e) {
          running = false;
        } catch (IOException e) {
          Printer.verbose("Couldn't write in file. Cause: %s", e.getMessage());
          failCount++;
        }
      }
      return 0;
    });
  }

  @Override
  boolean process(Element element) {
    String link = element.attributes().get("href");
    if (!link.trim().isEmpty() && !link.startsWith("#")) {
      linksQueue.add(link);
      Printer.print("Found link %s", link);
      return true;
    }
    return false;
  }

  @Override
  public void printResult() {
    Printer.print("The links were saved in %s", fileName);
    Printer.print("%d links were treated", getTreatedCount());
    if (failCount > 0) {
      Printer.print("%d links couldn't be saved", failCount);
    }
  }
}
