package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

public interface Saver {

  void printResult();

  void processElement(Element element);

}
