package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

public interface Saver {

  String ANY = "";

  void printResult();

  void processElement(Element element);

  void finish();

}
