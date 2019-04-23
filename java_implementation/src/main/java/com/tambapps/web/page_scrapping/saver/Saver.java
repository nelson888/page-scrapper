package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface Saver {

  void printResult();

  Optional<Future> process(ExecutorService executor, Element element);

}
