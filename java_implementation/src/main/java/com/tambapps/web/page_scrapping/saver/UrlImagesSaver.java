package com.tambapps.web.page_scrapping.saver;

import com.tambapps.web.page_scrapping.printing.Printer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

public class UrlImagesSaver {

  public static final int OK = 0, FILE_CREATION_ERROR = 1, SAVING_ERROR = 2;
  private static final int BUFFER_SIZE = 1024;

  private final ExecutorCompletionService<Integer> executorService;
  private final File directory;

  public UrlImagesSaver(Executor executor, File directory) {
    executorService = new ExecutorCompletionService<>(executor);
    this.directory = directory;
  }

  public Map<Integer, Integer> saveImages(List<URL> urls) {
    for (URL url : urls) {
      executorService.submit(new SaveTask(url));
    }

    HashMap<Integer, Integer> outputTasks = new HashMap<>();
    for (int i = 0; i < urls.size(); i++) {
      int returnCode;
      try {
        returnCode = executorService.take().get();
      } catch (InterruptedException | ExecutionException e) {
        Printer.verbose("Error while attempting to get result from image saving task: %s",
            e.getMessage());
        continue;
      }
      outputTasks.put(returnCode, outputTasks.getOrDefault(returnCode, 0) + 1);
    }
    return outputTasks;
  }

  private class SaveTask implements Callable<Integer> {

    private URL url;

    SaveTask(URL url) {
      this.url = url;
    }

    @Override
    public Integer call() {
      File file = getAvailableFile(directory, getFileName(url));
      try {
        if (!file.createNewFile()) {
          IOException exception = new IOException("Unknown error");
          Printer.verbose("Couldn't create new file: %s", exception.getMessage());
          throw exception;
        }
      } catch (IOException e) {
        Printer.verbose("Couldn't create new file: %s", e.getMessage());
        return FILE_CREATION_ERROR;
      }
      try (InputStream in = new BufferedInputStream(url.openStream());
          FileOutputStream out = new FileOutputStream(file)) {
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while (-1!=(n=in.read(buf))) {
          out.write(buf, 0, n);
        }
      } catch (IOException e) {
        Printer.verbose("Couldn't save image: %s", e.getMessage());
        return SAVING_ERROR;
      }
      return OK;
    }
  }

  private String getFileName(URL url) {
    String file = url.getFile();
    return file.substring(file.lastIndexOf('/') + 1);
  }

  private File getAvailableFile(File root, String name) {
    File file = new File(root, name);
    for (int i = 0; file.exists(); i++) {
      StringBuilder number = new StringBuilder(String.valueOf(i));
      while (number.length() < 3) {
        number.insert(0, '0');
      }
      String fileName;
      if (name.contains(".")) {
        int dotIndex = name.indexOf('.');
        fileName = name.substring(0, dotIndex) + '_' + number + name.substring(dotIndex);
      } else {
        fileName = name + '_' + number;
      }
      file = new File(root, fileName);
    }
    return file;
  }

}
