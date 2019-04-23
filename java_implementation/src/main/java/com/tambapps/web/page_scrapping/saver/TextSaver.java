package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

import java.io.File;

public class TextSaver extends FileSaver {

  private static final String FILENAME = "texts.txt";

  public TextSaver(File directory) {
    super(new File(directory, FILENAME));
  }

  @Override
  protected String mapToData(Element element) {
    String text = element.text();
    if (text == null) {
      return null;
    }
    text = text.trim();
    if (text.isEmpty()) {
      return null;
    }
    String tagName = element.tagName();
    return String.format("%s:  %s", tagName, text);
  }

  @Override
  protected String dataName() {
    return "texts";
  }
}
