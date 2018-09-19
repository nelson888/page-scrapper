package com.tambapps.web.page_scrapping.saver;

import com.tambapps.web.page_scrapping.printing.Printer;

import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class ImageSaver extends AbstractSaver {

  private final Map<Integer, Integer> errorsMap = new HashMap<>();

  public ImageSaver(Executor executor, File dir) {
    super(executor, dir, "img");
  }

  private static String getFileName(URL url) {
    String file = url.getFile();
    return file.substring(file.lastIndexOf('/') + 1);
  }

  @Override
  public boolean process(Element element) {
    executorService.submit(createTask(element));
    return true;
  }

  Callable<Integer> createTask(Element element) {
    return () -> {
      int returnCode = 0;
      String link = element.attributes().get("src");
      URL url;
      try {
        url = new URL(link);
      } catch (MalformedURLException e) {
        returnCode = 1;
        errorsMap.put(returnCode, errorsMap.getOrDefault(returnCode, 0) + 1);
        return returnCode;
      }
      File file = getAvailableFile(getFileName(url));
      try {
        Files.copy(url.openStream(), file.toPath());
      } catch (IOException e) {
        returnCode = 2;
        errorsMap.put(returnCode, errorsMap.getOrDefault(returnCode, 0) + 1);
        return returnCode;
      }
      return returnCode;
    };
  }

  @Override
  public void printResult() {
    Printer.log("%d images were treated", getTreatedCount());
    if (errorsMap.keySet().isEmpty()) {
      Printer.log("no error were encountered");
    } else {
      for (Integer errorCode : errorsMap.keySet()) {
        printError(errorCode);
      }
    }
  }

  private void printError(int errorCode) {
    int count = errorsMap.getOrDefault(errorCode, 0);
    String error;
    switch (errorCode) {
      default:
      case 1:
        error = "malformed url error";
        break;
      case 2:
        error = "error while writing image";
    }
    if (count > 0) {
      Printer.log("%d images couldn't be treated due to an %s", count, error);
    }
  }

}
