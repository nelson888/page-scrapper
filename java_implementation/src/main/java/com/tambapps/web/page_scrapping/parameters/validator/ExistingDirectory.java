package com.tambapps.web.page_scrapping.parameters.validator;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

public class ExistingDirectory implements IValueValidator<File> {

  @Override
  public void validate(String name, File value) throws ParameterException {
    if (!value.exists()) {
      throw new ParameterException("Directory with path" + value.getPath() + " doesn't exists");
    }
    if (!value.isDirectory()) {
      throw new ParameterException("File with path " + value.getPath() + " isn't a directory");
    }
  }

}
