package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class ImageSaver extends DirectorySaver implements LinkExtractor {

  private static final String DATA_NAME = "images";
  private static final String SRC_ATTRIBUTE = "src";

  public ImageSaver(File directory) {
    super(directory);
  }

  @Override
  protected String mapToData(Element element) {
    return extractLink(element, SRC_ATTRIBUTE);
  }

  @Override
  protected void save(String link) throws IOException {
    URL url = new URL(link);
    File file = getAvailableFile(getFileName(url));
    try (InputStream is = url.openStream()) {
      Files.copy(is, file.toPath());
    }
  }

  private static String getFileName(URL url) {
    String file = url.getFile();
    return file.substring(file.lastIndexOf('/') + 1);
  }

  @Override
  protected String dataName() {
    return DATA_NAME;
  }

}
