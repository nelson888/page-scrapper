package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

import java.io.File;

public class LinkSaver extends FileSaver implements LinkExtractor {

  private static final String DATA_NAME = "links";
  private static final String FILENAME = DATA_NAME + ".txt";
  private static final String HREF_ATTRIBUTE = "href";

  public LinkSaver(File directory) {
    super(new File(directory, FILENAME));
  }

  @Override
  protected String mapToData(Element element) {
    return extractLink(element, HREF_ATTRIBUTE);
  }

  @Override
  protected String dataName() {
    return DATA_NAME;
  }

}
