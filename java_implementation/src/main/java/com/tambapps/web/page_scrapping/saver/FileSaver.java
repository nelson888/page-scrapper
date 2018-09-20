package com.tambapps.web.page_scrapping.saver;

import com.tambapps.web.page_scrapping.util.Printer;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class FileSaver extends AbstractSaver {

  private final BlockingQueue<String> textQueue = new LinkedBlockingQueue<>();
  private static final String END = ""; //used to tell the worker thread that it must stop
  private final String fileName;
  private final String dataName;
  private int failCount = 0;

  FileSaver(Executor executor, File dir, String tag, String name, String dataName) {
    super(executor, dir, tag);
    this.dataName = dataName;
    File file = getAvailableFile(name);
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
      throw new RuntimeException("Couldn\'t instantiate file writer");
    }

    executorService.submit(() -> {
    while (true) {
      String text;
      try {
        text = textQueue.take();
      } catch (InterruptedException e) {
        return 1;
      }
      if (END == text) {
        return 0;
      }
      try {
        writer.write(text + "\n");
      } catch (IOException e) {
        Printer.verbose("Error while writing $text: %s", e.getMessage());
        failCount++;
      }
    }
        });
  }

  @Override
  protected final boolean process(Element element) {
    String text = processData(element);
    if (text != null) {
      Printer.verbose("Found %s %s", dataName, text);
      textQueue.add(text);
      return true;
    }
    return false;
  }

  abstract String processData(Element element);

  @Override
  public void finish() {
    textQueue.add(END);
    int returnCode;
    try {
      returnCode = executorService.take().get();
    } catch (InterruptedException | ExecutionException e) {
      returnCode = 1;
    }
    if (returnCode == 1) {
      Printer.print("The saving may not have finished because the process was interrupted");
    }
  }

  @Override
  public void printResult() {
    Printer.print("The %ss were saved in %s", dataName, fileName);
    Printer.print("%d %ss were treated", getTreatedCount(), dataName);
    if (failCount > 0) {
      Printer.print("$failCount %ss couldn't be saved", dataName);
    }
  }
}
