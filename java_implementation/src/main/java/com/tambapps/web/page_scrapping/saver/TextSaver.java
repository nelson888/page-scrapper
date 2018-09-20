package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

import java.io.File;
import java.util.concurrent.Executor;

public class TextSaver extends FileSaver {

  public TextSaver(Executor executor, File dir) {
    super(executor, dir, Saver.ANY, "text.txt", "text");
  }

  @Override
  String processData(Element element) {
    String text = element.text();
    if (text == null) {
      return null;
    }
    text = text.trim();
    String tagName = element.tagName();
    return String.format("%s: ``` %s ```", tagName, text);
  }

}
