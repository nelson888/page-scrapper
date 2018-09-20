package com.tambapps.web.page_scrapping.parameters;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import com.tambapps.web.page_scrapping.parameters.converter.ScrapingTypeConverter;
import com.tambapps.web.page_scrapping.parameters.validator.ExistingDirectory;
import com.tambapps.web.page_scrapping.parameters.validator.NotEmptyCollection;

import java.io.File;
import java.util.List;
import java.util.Set;

@Parameters(separators = "=")
public class Arguments {

  public static final String COLLECTION_SEPARATOR = ",";

  @Parameter(names = "-urls", description = "url(s) to scrap", required = true,
      validateWith = NotEmptyCollection.class)
  private List<String> urls;

  @Parameter(names = {"-dir", "--directory"}, converter = FileConverter.class,
      validateValueWith = ExistingDirectory.class,
      description = "The directory in which the images/links will be saved", required = true)
  private File directory;

  @Parameter(names = "-type", validateWith = NotEmptyCollection.class,
      converter = ScrapingTypeConverter.class,
      description = "scrap images and/or links", required = true)
  private Set<ScrapingType> types;

  @Parameter(names = {"-v", "--verbose"}, description = "get more output message")
  private boolean verbose;

  @Parameter(names = {"-t", "--threads"},
      description = "How many threads should be used to process image saving tasks")
  private int nbThreads = 3;


  public List<String> getUrls() {
    return urls;
  }

  public File getDirectory() {
    return directory;
  }

  public Set<ScrapingType> getTypes() {
    return types;
  }

  public boolean isVerboseEnabled() {
    return verbose;
  }

  public int getNbThreads() {
    return nbThreads;
  }
}
