package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

import java.util.stream.Stream;

interface LinkExtractor {

  String HTTP = "http";
  String WORLD_WIDE_WEB = "www";


  default String extractLink(Element element, String attributeName) {
    String link = element.attributes().get(attributeName);
    if (link != null) {
      link = link.trim();
      return isValidLink(link) ? link : null;
    }
    return null;
  }

  private boolean isValidLink(String link) {
    return link != null && Stream.of(HTTP, WORLD_WIDE_WEB).anyMatch(link::startsWith);
  }
}
