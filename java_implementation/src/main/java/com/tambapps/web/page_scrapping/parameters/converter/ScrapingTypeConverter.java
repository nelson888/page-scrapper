package com.tambapps.web.page_scrapping.parameters.converter;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.tambapps.web.page_scrapping.parameters.Arguments;
import com.tambapps.web.page_scrapping.parameters.ScrapingType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ScrapingTypeConverter implements IStringConverter<Set<ScrapingType>> {

  @Override
  public Set<ScrapingType> convert(String value) {
    return Arrays.stream(value.split(Arguments.COLLECTION_SEPARATOR))
        .map(this::toType)
        .collect(Collectors.toSet());
  }

  private ScrapingType toType(String t) {
    try {
      return ScrapingType.valueOf(t.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ParameterException(t + " is not a valid type", e);
    }
  }

}