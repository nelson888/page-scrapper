package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

abstract class AbstractSaver implements Saver {
  final ExecutorCompletionService<Integer> executorService;
  private final String tag;
  private final File dir;
  private int treatedCount;

  AbstractSaver(Executor executor, File dir, String tag) {
    this.tag = tag;
    this.dir = dir;
    treatedCount = 0;
    executorService = new ExecutorCompletionService<>(executor);
  }

  @Override
  public void processElement(Element element) {
    if (tag.equalsIgnoreCase(element.tagName())) {
      if (process(element)) {
        treatedCount++;
      }
    }
  }

  abstract boolean process(Element element);

  File getAvailableFile(String name) {
    File file = new File(dir, name);
    for (int i = 0; file.exists(); i++) {
      StringBuilder number = new StringBuilder(String.valueOf(i));
      while (number.length() < 3) { //max = 999
        number.insert(0, '0');
      }
      String fileName;
      if (name.contains(".")) {
        int dotIndex = name.indexOf('.');
        fileName = name.substring(0, dotIndex) + '_' + number + name.substring(dotIndex);
      } else {
        fileName = name + '_' + number;
      }
      file = new File(dir, fileName);
    }
    return file;
  }

  int getTreatedCount() {
    return treatedCount;
  }
}
