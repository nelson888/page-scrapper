package com.tambapps.web.page_scrapping.saver;

import com.tambapps.web.page_scrapping.util.Printer;

import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
    String link = element.attributes().get("src");
    if (isValidLink(link)) {
      executorService.submit(createTask(link));
      return true;
    }
    return false;
  }

  Callable<Integer> createTask(String link) {
    return () -> {
      int returnCode = 0;
      URL url;
      try {
        url = new URL(link);
      } catch (MalformedURLException e) {
        returnCode = 1;
        return returnCode;
      }
      File file = getAvailableFile(getFileName(url));
      try {
        Files.copy(url.openStream(), file.toPath());
      } catch (IOException e) {
        returnCode = 2;
        return returnCode;
      }
      return returnCode;
    };
  }

  @Override
  public void printResult() {
    Printer.print("%d images were treated", getTreatedCount());
    if (errorsMap.keySet().isEmpty()) {
      Printer.print("no error were encountered");
    } else {
      for (Integer errorCode : errorsMap.keySet()) {
        if (errorCode != 0) {
          printError(errorCode);
        }
      }
    }
  }

  private void printError(int errorCode) {
    int count = errorsMap.getOrDefault(errorCode, 2);
    String error;
    switch (errorCode) {
      default:
      case 1:
        error = "a malformed url error";
        break;
      case 2:
        error = "an error while writing image";
        break;
      case 3:
        error = "the interruption of the saving process";
        break;

    }
    if (count > 0) {
      Printer.print("%d images couldn't be treated due to %s", count, error);
    }
  }

  @Override
  public void finish() {
    for (int i = 0; i < getTreatedCount(); i++) {
      int returnCode;
      try {
        returnCode = executorService.take().get();
      } catch (InterruptedException e) {
        returnCode = 3;
      } catch (ExecutionException e) {
        returnCode = 2;
      }
      errorsMap.put(returnCode, errorsMap.getOrDefault(returnCode, 0) + 1);
    }
  }

}
