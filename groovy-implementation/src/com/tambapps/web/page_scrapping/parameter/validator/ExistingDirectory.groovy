package com.tambapps.web.page_scrapping.parameter.validator

import com.beust.jcommander.IValueValidator
import com.beust.jcommander.ParameterException

class ExistingDirectory implements IValueValidator<File> {

    @Override
    void validate(String name, File value) throws ParameterException {
        if (!value.exists()) {
            throw new ParameterException("Directory with path $value.path doesn't exists")
        }
        if (!value.isDirectory()) {
            throw new ParameterException("File with path $value.path isn't a directory")
        }
    }

}
