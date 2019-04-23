package com.tambapps.web.page_scrapping.saver;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractSaver implements Saver {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSaver.class);

  private final Map<Outcome, AtomicInteger> outcomeMap = Map.of(
      Outcome.SUCCESS, new AtomicInteger(0),
      Outcome.ERROR, new AtomicInteger(0));

  @Override
  public Optional<Future> process(ExecutorService executor, Element element) {
    String data = mapToData(element);
    if (data == null) {
      return Optional.empty();
    }
    Future future = executor.submit(() -> {
      try {
        save(data);
        outcomeMap.get(Outcome.SUCCESS).incrementAndGet();
      } catch (IOException e) {
        outcomeMap.get(Outcome.ERROR).incrementAndGet();
      }
    });
    return Optional.of(future);
  }

  protected abstract String mapToData(Element element);
  protected abstract void save(String data) throws IOException;
  protected abstract String dataName();

  @Override
  public void printResult() {
    String name = dataName();
    long total = outcomeMap.values().stream().mapToInt(AtomicInteger::get).sum();
    LOGGER.info("Processed {} {} in total", total, name);

    if (total == outcomeMap.get(Outcome.SUCCESS).get()) {
      LOGGER.info("All {} were saved successfully", name);
    } else {
      LOGGER.info("{} {} were saved successfully", outcomeMap.get(Outcome.SUCCESS), name);
      LOGGER.info("{} {} were not saved due to an error", outcomeMap.get(Outcome.ERROR), name);
    }
  }

}
