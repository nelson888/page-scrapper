package com.tambapps.web.page_scrapping.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class LazyExecutorSupplier implements Supplier<ExecutorService> {

  private final int nbThreads;
  private ExecutorService executor = null;

  public LazyExecutorSupplier(int nbThreads) {
    this.nbThreads = nbThreads;
  }

  @Override
  public ExecutorService get() {
    if (executor == null) {
      executor = Executors.newFixedThreadPool(nbThreads);
    }
    return executor;
  }

  public boolean wasSupplied() {
    return executor != null;
  }
}
