package com.tambapps.web.page_scrapping.parameters.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class NotEmptyCollection implements IParameterValidator {

  @Override
  public void validate(String name, String value) throws ParameterException {
    if (value.isEmpty()) {
      throw new ParameterException("Parameter " + name + " shouldn't be empty");
    }
  }

}