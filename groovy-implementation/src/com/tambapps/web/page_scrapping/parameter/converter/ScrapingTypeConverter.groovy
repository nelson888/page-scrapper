package com.tambapps.web.page_scrapping.parameter.converter

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.ParameterException
import com.tambapps.web.page_scrapping.parameter.Args
import com.tambapps.web.page_scrapping.parameter.ScrapingType

import java.util.stream.Collectors

class ScrapingTypeConverter implements IStringConverter<Set<ScrapingType>> {

  @Override
  Set<ScrapingType> convert(String value) {
    return Arrays.stream(value.split(Args.COLLECTION_SEPARATOR))
            .map({ toType(it) })
            .collect(Collectors.toSet())
  }

  private ScrapingType toType(String t) {
    try {
      return ScrapingType.valueOf(t.toUpperCase())
    } catch(IllegalArgumentException e) {
      throw new ParameterException("$t is not a valid types", e)
    }
  }

}
