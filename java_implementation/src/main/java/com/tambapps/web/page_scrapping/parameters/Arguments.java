package com.tambapps.web.page_scrapping.parameters;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(separators = "=")
public class Arguments {

  @Parameter(description = "url(s) to scrap", required = true)
  private List<String> urls;

  @Parameter(names = {"-dir", "--directory"}, description = "The directory in which the images/links will be saved", required = true)
  private String directory;

  @Parameter(names = "-type", description = "scrap images and/or links", required = true)
  private ScrappingType type;

  @Parameter(names = {"-v", "--verbose"}, description = "get more output message")
  private boolean verbose;

  @Parameter(names = {"-t", "--threads"},
      description = "How many threads should be used to process image saving tasks")
  private int nbThreads = 3;


  public List<String> getUrls() {
    return urls;
  }

  public String getDirectory() {
    return directory;
  }

  public ScrappingType getType() {
    return type;
  }

  public boolean isVerboseEnabled() {
    return verbose;
  }

  public int getNbThreads() {
    return nbThreads;
  }
}
