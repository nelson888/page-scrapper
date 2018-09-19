package com.tambapps.web.page_scrapping.parameters.validator;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class NaturalInteger implements IValueValidator<Integer> {

  @Override
  public void validate(String name, Integer n) throws ParameterException {
    if (n <= 0) {
      throw new ParameterException("Parameter " + name + " should be natural (found " + n + ")");
    }
  }
}
