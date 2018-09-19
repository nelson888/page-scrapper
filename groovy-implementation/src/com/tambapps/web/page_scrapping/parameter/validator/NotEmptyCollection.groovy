package com.tambapps.web.page_scrapping.parameter.validator

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

class NotEmptyCollection implements IParameterValidator {

    @Override
    void validate(String name, String value) throws ParameterException {
        if (value.isEmpty()) {
            throw new ParameterException("Parameter $name shouldn't be empty")
        }
    }

}