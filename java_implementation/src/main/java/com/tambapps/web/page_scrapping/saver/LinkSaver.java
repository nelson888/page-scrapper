package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class LinkSaver extends FileSaver {

  public LinkSaver(Executor executor, File dir) {
    super(executor, dir, "a", "links.txt", "link");
  }

  @Override
  String processData(Element element) {
    String link = element.attributes().get("href");
    if (link != null) {
      link = link.trim();
    }
    if (link != null && !link.isEmpty() &&
        Stream.of("http", "www").anyMatch(link::startsWith)) {
      return link;
    }
    return null;
  }

}
