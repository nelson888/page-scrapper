package com.tambapps.web.page_scrapping.saver;

import java.io.File;

public abstract class DirectorySaver  extends AbstractSaver {

  private final File directory;

  public DirectorySaver(File directory) {
    this.directory = directory;
  }

  File getAvailableFile(String name) {
    File file = new File(directory, name);
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
      file = new File(directory, fileName);
    }
    return file;
  }

}
