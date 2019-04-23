package com.tambapps.web.page_scrapping.saver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class FileSaver extends AbstractSaver {

  private final File file;

  public FileSaver(File file) {
    this.file = file;
  }

  @Override
  protected void save(String data) throws IOException {
    try (FileWriter writer = new FileWriter(file, true)) {
      writer.write(data + "\n");
    }
  }

}
